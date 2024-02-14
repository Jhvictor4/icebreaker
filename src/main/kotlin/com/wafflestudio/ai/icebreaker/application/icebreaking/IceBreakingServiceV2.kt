package com.wafflestudio.ai.icebreaker.application.icebreaking

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.exception.OpenAITimeoutException
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.message.MessageId
import com.aallam.openai.api.run.RequiredAction
import com.aallam.openai.api.run.Run
import com.aallam.openai.api.run.ToolOutput
import com.aallam.openai.api.thread.Thread
import com.aallam.openai.client.OpenAI
import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.ai.icebreaker.application.*
import com.wafflestudio.ai.icebreaker.application.common.ChatGptPort
import com.wafflestudio.ai.icebreaker.application.user.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Component

@Component
@OptIn(BetaOpenAI::class)
class IceBreakingServiceV2(
    private val chatGptPort: ChatGptPort,
    private val iceBreakingToolsCallableRegistry: IceBreakingToolsCallableRegistry,
    private val objectMapper: ObjectMapper,
    private val openAI: OpenAI
) {
    private val recentRunCache = mutableMapOf<String, Run>()

    fun getIceBreakingQuestions(
        userA: User,
        userB: User,
        useGpt4: Boolean = false
    ) = flow {
        val messages = mutableMapOf<MessageId, Message>()
        val thread = chatGptPort.initThread(userA, userB)

        // TODO 메시지 있으면 바로 반환
        messages.putAll(openAI.messages(thread.id).associateBy { it.id })
        if (messages.size > 1) {
            messages.values.forEach { emit(it) }
            return@flow
        }

//        val thread = openAI.thread(ThreadId("thread_s2wgzp8Fyry9wUNeGJ6iApsE"))!!
        val initialRun = chatGptPort.createRun(thread)
        val runId = initialRun.id
        logger.info { "RunId: $runId, ThreadId: ${thread.id}" }

        // cache
        recentRunCache["${userA.id}-${userB.id}"] = initialRun

        do {
            var updatedRun: Run? = null

            try {
                openAI.messages(thread.id).run {
                    val new = this.filter { it.id !in messages.keys && it.textContent().isNotEmpty() }.distinct()
                    if (new.isNotEmpty()) {
                        messages.putAll(new.associateBy { it.id })
                        logger.info { "New Messages: \n $new" }
                        new.forEach { emit(it) }
                    }
                }

                val run = chatGptPort.run(thread.id, runId)
                updatedRun = when (run.status) {
                    Status.Completed -> break
                    Status.RequiresAction -> action(thread, run, userA, userB)
                    else -> {
                        delay(300); run
                    }
                }

                recentRunCache["${userA.id}-${userB.id}"] = updatedRun
            } catch (e: OpenAITimeoutException) {
                logger.error { "OpenAI Timeout: $e" }
            }

            if (updatedRun == null) {
                updatedRun = recentRunCache["${userA.id}-${userB.id}"]
            }
        } while (
            updatedRun?.status != Status.Completed
        )

        // repeat 1 more
        openAI.messages(thread.id).run {
            val new = this.filter { it.id !in messages.keys }.distinct()
            new.forEach { emit(it) }
        }
    }

    private suspend fun action(thread: Thread, run: Run, userA: User, userB: User): Run {
        val requiredAction = run.requiredAction
        check(requiredAction is RequiredAction.SubmitToolOutputs)
        val outputs = requiredAction.toolOutputs.toolCalls.map {
            val toolCallAsFunctionCall = it as ToolCall.Function
            val response = iceBreakingToolsCallableRegistry[it.function.name].invoke(ToolCallWrapper(toolCallAsFunctionCall, userA, userB))
            ToolOutput(toolCallAsFunctionCall.id, response)
        }

        return openAI.submitToolOutput(thread.id, run.id, outputs).also {
            logger.info { "Submitted tool outputs: $outputs" }
        }
    }

    companion object : Log
}

@OptIn(BetaOpenAI::class)
fun Message.textContent(): String {
    val contents = content
    return contents.joinToString("\n") {
        when (it) {
            is MessageContent.Text -> it.text.value
            else -> throw IllegalStateException("Unexpected message content: $content")
        }
    }
}
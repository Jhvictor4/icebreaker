package com.wafflestudio.ai.icebreaker.outbound.common

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.run.Run
import com.aallam.openai.api.run.RunId
import com.aallam.openai.api.run.RunRequest
import com.aallam.openai.api.thread.Thread
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.api.thread.ThreadMessage
import com.aallam.openai.api.thread.ThreadRequest
import com.aallam.openai.client.OpenAI
import com.wafflestudio.ai.icebreaker.application.Log
import com.wafflestudio.ai.icebreaker.application.SUMMARIZE_PROMPT
import com.wafflestudio.ai.icebreaker.application.common.ChatGptPort
import com.wafflestudio.ai.icebreaker.application.common.ChatGptResponseDto
import com.wafflestudio.ai.icebreaker.application.user.User
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
@OptIn(BetaOpenAI::class)
class ChatGptAdapter(
    private val openAI: OpenAI,
    @Value("\${openai.model}")
    private val model: String
) : ChatGptPort {

    private val threadCache = mutableMapOf<String, Thread>()

    private val assistant = runBlocking {
        logger.info { "Initializing OpenAI Assistant.." }
        openAI.assistant(id = AssistantId("asst_Hoi8Bla9mPdtCtV1VoOM5Z5N"))
            .also { logger.info { "Initialized OpenAI Assistant" } }
    }

    override suspend fun initThread(userA: User, userB: User): Thread {
        val key = "${userA.id}-${userB.id}"
        return openAI.thread(ThreadRequest(messages = listOf(ThreadMessage(Role.User, SUMMARIZE_PROMPT(userA, userB)))))
            .also { threadCache[key] = it }
    }

    override suspend fun getThread(userA: User, userB: User): Thread? {
        val key = "${userA.id}-${userB.id}"
        return threadCache[key] ?: initThread(userA, userB)
    }

    override suspend fun createRun(thread: Thread): Run {
        return openAI.createRun(
            threadId = thread.id,
            request = RunRequest(assistantId = assistant!!.id)
        )
    }

    override suspend fun run(threadId: ThreadId, runId: RunId): Run {
        return openAI.getRun(
            threadId = threadId,
            runId = runId
        )
    }

    override suspend fun createChat(
        prompt: String,
        conversations: List<ChatMessage>,
        tools: List<Tool>,
        specificModel: String?
    ): ChatGptResponseDto? {
        val rawResponse = try {
            openAI.chatCompletion(
                request = ChatCompletionRequest(
                    model = ModelId(specificModel ?: model),
                    messages = prompt
                        .takeIf { it.isNotEmpty() }
                        ?.let { ChatMessage(Role.System, it) }
                        ?.let(::listOf)
                        .orEmpty() +
                            conversations,
                    tools = tools.takeIf { it.isNotEmpty() },
                    toolChoice = ToolChoice.Auto.takeIf { tools.isNotEmpty() }
                )
            )
                .choices
                .firstOrNull()
                ?.message
                ?: return null
        } catch (e: Exception) {
            logger.error { "Failed to call OpenAI Chat API: $e" }
            return null
        }

        if (rawResponse.toolCalls.orEmpty().isNotEmpty()) {
            val call = rawResponse.toolCalls!!.first()
            return ChatGptResponseDto.FunctionCall(call = call as ToolCall.Function)
        }

        return when (
            val response = rawResponse.messageContent
        ) {
            is TextContent -> ChatGptResponseDto.Message(
                message = response.content,
                imageFileUrls = emptyList()
            )
            is ListContent -> {
                val (texts, images) = response.content.partition { it is TextPart }
                ChatGptResponseDto.Message(
                    message = texts.joinToString("\n") { (it as TextPart).text },
                    imageFileUrls = images.map { (it as ImagePart).imageUrl.url }
                )
            }
            null -> null
        }
    }

    companion object : Log
}

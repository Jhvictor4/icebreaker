package com.wafflestudio.ai.icebreaker.application.icebreaking

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.Role
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.wafflestudio.ai.icebreaker.application.*
import com.wafflestudio.ai.icebreaker.application.common.ChatGptPort
import com.wafflestudio.ai.icebreaker.application.common.ChatGptResponseDto
import com.wafflestudio.ai.icebreaker.application.user.User
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class IceBreakingService(
    private val chatGptPort: ChatGptPort,
    private val iceBreakingToolsCallableRegistry: IceBreakingToolsCallableRegistry,
    private val objectMapper: ObjectMapper
) {
    private val localCache = mutableMapOf<String, List<ChatMessage>>()

    data class Result(
        val result: List<String>,
        val chats: List<ChatMessage>,
        val actionsDone: List<ActionResult>
    )

    suspend fun getIceBreakingQuestions(
        userA: User,
        userB: User,
        useGpt4: Boolean = false // 테스트용 파라미터..
    ): Result {
        val cacheKey = "${userA.id}-${userB.id}-${LocalDate.now()}" // 하루 단위 캐싱
        val systemPrompt = ChatMessage(ChatRole.System, iceBreakingSystemPrompt())
        val summarizePrompt = ChatMessage(ChatRole.User, summarizePrompt(userA, userB))
//        val chats = (listOf(systemPrompt, summarizePrompt) + localCache[cacheKey].orEmpty()).toMutableList()
        val chats = mutableListOf(systemPrompt, summarizePrompt)
        val actionsDone = mutableListOf<ActionResult>()

        var upperBound = 9
        while (true) {
            val prompt = planningPrompt(actionsDone)
            when (
                val response = chatGptPort.createChat(
                    prompt = prompt,
                    conversations = chats,
                    tools = IceBreakingTools.entriesAsChatGptTools,
                    specificModel = if (useGpt4) "gpt-4-0125-preview" else null
                )
                    .also { upperBound-- }
                    .also { chats.add(ChatMessage(Role.User, prompt)) }
                    .also {
                        when (it) {
                            is ChatGptResponseDto.Message -> {
                                logger.debug { "Response [message]:\n${it.message}" }
                            }
                            is ChatGptResponseDto.FunctionCall -> {
                                logger.debug { "Response [function call]:\n${it.call.function.nameOrNull} | ${it.call.function.argumentsOrNull}" }
                            }
                            null -> null
                        }
                    }
            ) {
                is ChatGptResponseDto.Message -> {
                    if (isResultAnswer(response.message)) {
                        return Result(extract(response.message), chats, actionsDone)
                    }

                    chats.add(ChatMessage(ChatRole.User, response.message))
//                    actionsDone.add(ActionResult("just thinking", null, response.message))
                }
                is ChatGptResponseDto.FunctionCall -> {
                    val tool = iceBreakingToolsCallableRegistry[response.call.function.name]
                    val toolResponse = tool.invoke(ToolCallWrapper(response.call, userA, userB))

                    if (response.call.function.name == "generateResult") {
                        return Result(taskResult(toolResponse), chats, actionsDone)
                    }

                    chats.add(ChatMessage(ChatRole.System, toolResponse))
                    actionsDone.add(ActionResult(response.call.function.nameOrNull, response.call.function.argumentsOrNull, toolResponse))
                }
            }

            if (upperBound <= 0) {
                logger.warn { "Failed to get ice breaking questions" }
                return Result(listOf("Fallback"), chats, actionsDone) // TODO fallback
            }
        }
    }

    // 성능 쉣쉣쉣
    private fun taskResult(response: String): List<String> =
        runCatching {
            objectMapper.readValue<FinalQuestions>(response).result
        }.getOrElse {
            logger.error { "Failed to parse result: $response" }
            return listOf(response) // TODO fallback
        }

    // 에휴..
    fun isResultAnswer(input: String): Boolean {
        return input.contains("<RESPONSE>")
    }

    fun extract(input: String): List<String> {
        val regex = Regex("(?s)<RESPONSE>(.*?)</RESPONSE>")
        val matches = regex.findAll(input)
        return matches.map { it.groupValues[1].trim() }
            .firstOrNull()
            ?.let { objectMapper.readValue<FinalQuestions>(it).result }
            ?: listOf(input)
    }

    companion object : Log
}

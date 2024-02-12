package com.wafflestudio.ai.icebreaker.outbound.common

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.wafflestudio.ai.icebreaker.application.Log
import com.wafflestudio.ai.icebreaker.application.common.ChatGptPort
import com.wafflestudio.ai.icebreaker.application.common.ChatGptResponseDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ChatGptAdapter(
    private val openAI: OpenAI,
    @Value("\${openai.model}")
    private val model: String
) : ChatGptPort {

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

    companion object: Log
}

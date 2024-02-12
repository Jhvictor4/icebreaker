package com.wafflestudio.ai.icebreaker.outbound.common

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.wafflestudio.ai.icebreaker.application.common.ChatGptPort
import com.wafflestudio.ai.icebreaker.application.common.ChatGptResponseDto
import org.springframework.stereotype.Component

@Component
class ChatGptAdapter(
    private val openAI: OpenAI
) : ChatGptPort {
    override suspend fun createChat(
        prompt: String,
        conversations: List<ChatMessage>,
        tools: List<Tool>
    ): ChatGptResponseDto? {
        val rawResponse = openAI.chatCompletion(
            request = ChatCompletionRequest(
                model = ModelId("gpt-3.5-turbo"),
                messages = prompt
                    .takeIf { it.isNotEmpty() }
                    ?.let { ChatMessage(Role.System, it) }
                    ?.let(::listOf)
                    .orEmpty() +
                    conversations,
                tools = tools,
                toolChoice = ToolChoice.Auto
            )
        )
            .choices
            .firstOrNull()
            ?.message
            ?: return null

        if (rawResponse.toolCalls.orEmpty().isNotEmpty()) {
            val call = rawResponse.toolCalls!!.first()
            return ChatGptResponseDto.FunctionCall(call = call)
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
}

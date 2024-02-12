package com.wafflestudio.ai.icebreaker.outbound.common

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.wafflestudio.ai.icebreaker.application.common.ChatGptConversationDto
import com.wafflestudio.ai.icebreaker.application.common.ChatGptMessageResponseDto
import com.wafflestudio.ai.icebreaker.application.common.ChatGptPort
import com.wafflestudio.ai.icebreaker.application.understanding.Understanding
import org.springframework.stereotype.Component

@Component
class ChatGptAdapter(
    private val openAI: OpenAI
) : ChatGptPort {
    override suspend fun createChat(
        prompt: String,
        conversations: List<ChatGptConversationDto>
    ): ChatGptMessageResponseDto? {
        val response = openAI.chatCompletion(
            request = ChatCompletionRequest(
                model = ModelId("gpt-3.5-turbo"),
                messages = prompt
                    .takeIf { it.isNotEmpty() }
                    ?.let { ChatMessage(Role.System, it) }
                    ?.let(::listOf)
                    .orEmpty()
                    + conversations.map {
                    ChatMessage(role = it.role, content = it.message)
                }
            )
        )
            .choices
            .firstOrNull()
            ?.message
            ?.messageContent
            ?: return null

        return when (response) {
            is TextContent -> ChatGptMessageResponseDto(
                message = response.content,
                imageFileUrls = emptyList()
            )
            is ListContent -> {
                val (texts, images) = response.content.partition { it is TextPart }
                ChatGptMessageResponseDto(
                    message = texts.joinToString("\n") { (it as TextPart).text },
                    imageFileUrls = images.map { (it as ImagePart).imageUrl.url }
                )
            }
        }
    }

}

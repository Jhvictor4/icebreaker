package com.wafflestudio.ai.icebreaker.application.outbound

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.wafflestudio.ai.icebreaker.application.understanding.Understanding
import org.springframework.stereotype.Component

interface ChatGptPort {
    suspend fun createChat(
        prompt: String,
        conversations: List<ChatGptConversationDto> = emptyList(),
    ): ChatGptMessageResponseDto?
}

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
                messages = initialPrompt + conversations.map {
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

    private val initialPrompt = """
        You are a helpful assistant that find users' characteristics and life experiences from social activities.
        You are asked to find any helpful information for people to understand about the user.
        You are given certain kind of images, web pages, and social media posts and then you are asked to find the user's characteristics and life experiences.
        You need to categorize what you've found and provide a summary of the user's characteristics and life experiences.
        Available Categories are as follows:
        ${Understanding.values().joinToString(", ") { it.name }}
        
        After freely categorizing the user's characteristics and life experiences, provide a summary of the user's characteristics and life experiences.
        Result Format should be as follows (example):
        <RESULT>
        {"understanding": "${Understanding.LIFE_EXPERIENCE}", "content": "The user has a lot of experience in traveling and has a lot of friends."}
        {"understanding": "${Understanding.OUTDOOR_ACTIVITY}", "content": "The user have gone to hiking and camping in September 8th, 2023."}
        <RESULT>
    """.trimIndent()
        .let { ChatMessage(Role.System, it) }
        .let(::listOf)
}

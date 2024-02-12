package com.wafflestudio.ai.icebreaker.outbound.common

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.wafflestudio.ai.icebreaker.application.Log
import com.wafflestudio.ai.icebreaker.application.common.*
import org.springframework.stereotype.Component

@Component
class ChatGptAdapter(
    private val openAI: OpenAI
) : ChatGptPort {
    override suspend fun createChat(
        prompt: String,
        conversations: List<ChatGptConversationDto>,
        initialPrompt: String
    ): ChatGptMessageResponseDto? {
        val chatMessages = listOfNotNull(
            initialPrompt.takeUnless { it.isEmpty() },
            prompt.takeUnless { it.isEmpty() }
        ).map { ChatMessage(Role.User, it) } + conversations.map {
            ChatMessage(role = it.role, content = it.message)
        }

        if (chatMessages.isEmpty()) return null

        val rawResponse = try {
            openAI.chatCompletion(
                request = ChatCompletionRequest(
                    model = ModelId("gpt-3.5-turbo"),
                    messages = chatMessages
                )
            )
        } catch (e: Exception) {
            logger.error { "Failed to create chat: ${e.message}" }
            return null
        }

        val response = rawResponse
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

    override suspend fun selfDiscovery(prompt: String): ChatGptMessageResponseDto? {
        val selectedModules = createChat(SELECT_STEP_PROMPT(prompt))?.message!!
        logger.debug { "Selected modules: $selectedModules" }
        val adaptedModules = createChat(ADAPT_STEP_PROMPT(selectedModules, prompt))?.message!!
        logger.debug { "Adapted modules: $adaptedModules" }
        val implementedStructure = createChat(IMPLEMENT_STEP_PROMPT(adaptedModules, prompt))?.message!!
        logger.debug { "Implemented structure: $implementedStructure" }
        return createChat(EXECUTE_STEP_PROMPT(implementedStructure, prompt))
    }

    companion object : Log
}

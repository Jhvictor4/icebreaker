package com.wafflestudio.ai.icebreaker.application.common

interface ChatGptPort {
    suspend fun createChat(
        prompt: String,
        conversations: List<ChatGptConversationDto> = emptyList()
    ): ChatGptMessageResponseDto?
}

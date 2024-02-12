package com.wafflestudio.ai.icebreaker.application.common

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.Tool

interface ChatGptPort {
    suspend fun createChat(
        prompt: String,
        conversations: List<ChatMessage> = emptyList(),
        tools: List<Tool> = emptyList()
    ): ChatGptResponseDto?
}

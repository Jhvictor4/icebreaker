package com.wafflestudio.ai.icebreaker.application.common

import com.aallam.openai.api.core.Role

data class ChatGptConversationDto(
    val role: Role,
    val message: String
)

data class ChatGptMessageResponseDto(
    val message: String,
    val imageFileUrls: List<String>
)

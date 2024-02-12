package com.wafflestudio.ai.icebreaker.application.common

import com.aallam.openai.api.chat.ToolCall

interface ChatGptResponseDto {
    data class Message(
        val message: String,
        val imageFileUrls: List<String>
    ) : ChatGptResponseDto

    data class FunctionCall(
        val call: ToolCall.Function
    ) : ChatGptResponseDto
}

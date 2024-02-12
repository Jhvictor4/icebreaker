package com.wafflestudio.ai.icebreaker.application.common

import com.wafflestudio.ai.icebreaker.application.understanding.Understanding

interface ChatGptPort {
    suspend fun createChat(
        prompt: String,
        conversations: List<ChatGptConversationDto> = emptyList(),
        initialPrompt: String = ""
    ): ChatGptMessageResponseDto?

    suspend fun selfDiscovery(
        prompt: String
    ): ChatGptMessageResponseDto?
}

internal val understandingPrompt = """
        You are a helpful assistant that find users' characteristics and life experiences from social activities.
        You are asked to find any helpful information for people to understand about the user.
        You are given certain kind of images, web pages, and social media posts and then you are asked to find the user's characteristics and life experiences.
        You need to categorize what you've found and provide a summary of the user's characteristics and life experiences.
        Available Categories are as follows:
        ${Understanding.values().joinToString(", ") { it.name }}
        
        After freely categorizing the user's characteristics and life experiences, provide a summary of the user's characteristics and life experiences.
        Result Format should be as follows (example):
        <RESULT>
        {"understanding": "${Understanding.LIFE_EXPERIENCE}", "content": "2003년생 최유진 has a lot of experience in traveling and has a lot of friends."}
        {"understanding": "${Understanding.OUTDOOR_ACTIVITY}", "content": "The user have gone to hiking and camping in September 8th, 2023."}
        <RESULT>
""".trimIndent()

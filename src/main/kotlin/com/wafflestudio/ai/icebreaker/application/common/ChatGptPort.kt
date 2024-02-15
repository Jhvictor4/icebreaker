package com.wafflestudio.ai.icebreaker.application.common

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.run.Run
import com.aallam.openai.api.run.RunId
import com.aallam.openai.api.thread.Thread
import com.aallam.openai.api.thread.ThreadId
import com.wafflestudio.ai.icebreaker.application.user.User

@OptIn(BetaOpenAI::class)
interface ChatGptPort {
    suspend fun createChat(
        prompt: String,
        conversations: List<ChatMessage> = emptyList(),
        tools: List<Tool> = emptyList(),
        specificModel: String? = null,
        maxToken: Int? = null,
    ): ChatGptResponseDto?

    suspend fun initThread(userA: User, userB: User): Thread

    suspend fun getThread(userA: User, userB: User): Thread?

    suspend fun createRun(thread: Thread): Run

    suspend fun run(threadId: ThreadId, runId: RunId): Run
}

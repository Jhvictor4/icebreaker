package com.wafflestudio.ai.icebreaker.application.icebreaking

interface IceBreakingStreamResponse {
    data class Thought(
        val thought: String
    ) : IceBreakingStreamResponse

    data class Question(
        val question: String,
        val keywords: List<String>
    )

    data class FinalQuestion(
        val result: List<Question>
    ) : IceBreakingStreamResponse
}

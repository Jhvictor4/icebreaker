package com.wafflestudio.ai.icebreaker.application.icebreaking

sealed interface IceBreakingStreamResponse {
    data class Thought(
        val thought: String
    ) : IceBreakingStreamResponse

    data class Question(
        val question: String,
        val keywords: List<String>
    )

    data class Result(
        val question: String,
        val keywords: List<String>,
        val cardNo: Int,
    )

    data class FinalQuestion(
        val result: List<Result>
    ) : IceBreakingStreamResponse
}

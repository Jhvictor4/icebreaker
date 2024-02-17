package com.wafflestudio.ai.icebreaker.api

import com.wafflestudio.ai.icebreaker.application.Log
import com.wafflestudio.ai.icebreaker.application.common.ChatGptResponseDto
import com.wafflestudio.ai.icebreaker.application.understanding.UnderstandingUseCase
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class ApiController(
    private val useCase: UnderstandingUseCase
) {

    @PostMapping("/understanding/by-uri")
    fun understand(
        @RequestBody request: UnderstandByUriRequest
    ): ChatGptResponseDto? {
        return useCase.understandByUri(request.uri)
    }

    data class UnderstandByUriRequest(
        val uri: String
    )

    companion object : Log
}

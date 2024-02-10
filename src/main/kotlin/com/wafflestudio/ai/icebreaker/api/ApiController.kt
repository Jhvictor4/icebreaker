package com.wafflestudio.ai.icebreaker.api

import com.wafflestudio.ai.icebreaker.application.common.ChatGptMessageResponseDto
import com.wafflestudio.ai.icebreaker.application.understanding.UnderstandingUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class ApiController(
    private val useCase: UnderstandingUseCase
) {

    data class UnderstandByUriRequest(
        val uri: String
    )

    @PostMapping("/understanding/by-uri")
    fun understand(
        @RequestBody request: UnderstandByUriRequest
    ): ChatGptMessageResponseDto? {
        return useCase.understandByUri(request.uri)
    }
}

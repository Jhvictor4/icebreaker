package com.wafflestudio.ai.icebreaker.api

import com.fasterxml.jackson.annotation.JsonFormat
import com.wafflestudio.ai.icebreaker.application.Log
import com.wafflestudio.ai.icebreaker.application.common.ChatGptResponseDto
import com.wafflestudio.ai.icebreaker.application.understanding.UnderstandingUseCase
import com.wafflestudio.ai.icebreaker.application.user.UserInformation
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

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

    @PostMapping("/user/basicInformation")
    fun addBasicInformation(
        @RequestBody basicInformation: BasicInformation
    ) {
        logger.info { "[addBasicInformation] basicInformation = $basicInformation" }
    }

    data class UnderstandByUriRequest(
        val uri: String
    )

    data class BasicInformation(
        val name: String?,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        val birthDay: LocalDateTime?,
        val gender: UserInformation.Gender?,
        val mbti: UserInformation.MBTI?,
        val major: String?
    )

    data class SnsInformation(
        val instagramUserIds: List<String> = emptyList(),
        val blogUserIds: List<String> = emptyList()
    )

    companion object : Log
}

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

    @GetMapping("/user")
    fun getUser(): UserResponse {
        return UserResponse(
            basicInformation = BasicInformation(
                name = "지혁",
                birthDay = LocalDateTime.now(),
                gender = UserInformation.Gender.MALE,
                mbti = UserInformation.MBTI.ENTJ,
                major = "컴퓨터공학부",
            ),
            imageUrls = listOf("https://some.domain.name/path"),
            snsInformation = SnsInformation()
        )
    }

    @PostMapping("/understanding/by-uri")
    fun understand(
        @RequestBody request: UnderstandByUriRequest
    ): ChatGptResponseDto? {
        return useCase.understandByUri(request.uri)
    }

    @PostMapping("/user/login")
    fun login(): LoginResponse {
        // TODO: session id 생성 및 저장
        return LoginResponse("session-id")
    }

    @PostMapping("/user/basicInformation")
    fun addBasicInformation(
        @RequestBody basicInformation: BasicInformation,
    ) {
        logger.info { "[addBasicInformation] basicInformation = $basicInformation" }
    }

    data class UnderstandByUriRequest(
        val uri: String
    )

    data class LoginResponse(
        val sessionId: String,
    )

    data class UserResponse(
        val basicInformation: BasicInformation,
        val imageUrls: List<String> = emptyList(),
        val snsInformation: SnsInformation,
    )

    data class BasicInformation(
        val name: String?,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        val birthDay: LocalDateTime?,
        val gender: UserInformation.Gender?,
        val mbti: UserInformation.MBTI?,
        val major: String?,
    )

    data class SnsInformation(
        val instagramUserIds : List<String> = emptyList(),
        val blogUserIds: List<String> = emptyList(),
    )

    companion object : Log

}

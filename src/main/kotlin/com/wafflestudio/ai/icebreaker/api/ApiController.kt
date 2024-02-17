package com.wafflestudio.ai.icebreaker.api

import com.fasterxml.jackson.annotation.JsonFormat
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.wafflestudio.ai.icebreaker.application.Log
import com.wafflestudio.ai.icebreaker.application.common.ChatGptResponseDto
import com.wafflestudio.ai.icebreaker.application.understanding.UnderstandingUseCase
import com.wafflestudio.ai.icebreaker.application.user.UserInformation
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.ByteArrayOutputStream
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

    @GetMapping("/user/qr")
    fun getQRCode(): ResponseEntity<ByteArray> {
        val url = "https://naver.com"
        val encode = MultiFormatWriter()
            .encode(url, BarcodeFormat.QR_CODE, 200, 200)

        val out = ByteArrayOutputStream()
        MatrixToImageWriter.writeToStream(encode, "PNG", out)

        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(out.toByteArray())
    }

    data class UnderstandByUriRequest(
        val uri: String
    )

    data class LoginResponse(
        val sessionId: String
    )

    data class UserResponse(
        val basicInformation: BasicInformation,
        val imageUrls: List<String> = emptyList(),
        val snsInformation: SnsInformation
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

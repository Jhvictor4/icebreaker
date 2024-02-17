package com.wafflestudio.ai.icebreaker.api

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.wafflestudio.ai.icebreaker.application.Log
import com.wafflestudio.ai.icebreaker.application.meetup.*
import com.wafflestudio.ai.icebreaker.application.user.User
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.ByteArrayOutputStream

@RestController
@RequestMapping("/api/v1/meet")
class MeetUpApiController(
    private val meetUpService: MeetUpService,
    private val requestMeetUpService: RequestMeetUpService
) {
    /**
     * User A can check whether he has a meet-up request
     */
    @GetMapping("/request/status")
    fun getRequestStatus(user: User): MeetUpRequestStatusDto {
        return requestMeetUpService.myMeetUpRequestStatus(user.id)
    }

    /**
     * User A requests a meet-up
     */
    @PostMapping("/request")
    fun requestMeetUp(user: User): MeetUpId {
        return requestMeetUpService.requestMeetUp(user.id)
    }

    /**
     * User A shares QR with a meet-up id
     */
    @Deprecated("클라에서 QR 직접")
    @PostMapping("/make-qr")
    fun getQRCode(
        user: User,
        @RequestParam meetUpId: String
    ): ResponseEntity<ByteArray> {
        val meetUpUrl = requestMeetUpService.createMeetUpUrl(user.id, MeetUpId(meetUpId))
        return buildAndReturnQR(meetUpUrl)
    }

    /**
     * User A can check whether anyone has joined his meet-up
     */
    @GetMapping("/meet")
    suspend fun getMeetUpStatus(
        user: User,
        @RequestParam meetUpId: String
    ): MeetUp? {
        return meetUpService.myMeetUpStatus(user.id, meetUpId)
    }

    /**
     * User B joins User A's meet-up
     */
    @PostMapping("/join")
    suspend fun joinMeetUp(
        @RequestParam meetUpId: String,
        user: User
    ): MeetUp {
        return meetUpService.joinMeetUpUrl(user, meetUpId)
    }

    private fun buildAndReturnQR(url: String): ResponseEntity<ByteArray> {
        val bitMatrix = MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, 200, 200)
        val byteArrayOutputStream = ByteArrayOutputStream()
        MatrixToImageWriter.writeToStream(bitMatrix, MediaType.IMAGE_PNG.getSubtype(), byteArrayOutputStream)
        return ResponseEntity.ok(byteArrayOutputStream.toByteArray())
    }

    companion object : Log
}

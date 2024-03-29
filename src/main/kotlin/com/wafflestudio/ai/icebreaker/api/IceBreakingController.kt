package com.wafflestudio.ai.icebreaker.api

import com.wafflestudio.ai.icebreaker.application.icebreaking.*
import com.wafflestudio.ai.icebreaker.application.meetup.MeetUpId
import com.wafflestudio.ai.icebreaker.application.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.asFlux
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/api/v1/icebreaking")
class IceBreakingController(
    private val iceBreakingStreamService: IceBreakingStreamService
) {
    @PostMapping("/generate", produces = ["application/stream+json"])
    suspend fun generateIceBreakingQuestion(
        user: User,
        @RequestBody meetUpId: MeetUpId
    ): Flow<IceBreakingStreamResponse> {
        return iceBreakingStreamService.getIceBreakingStream(user.id, meetUpId)
    }
}

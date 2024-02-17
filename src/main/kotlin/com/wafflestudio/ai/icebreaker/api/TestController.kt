package com.wafflestudio.ai.icebreaker.api

import com.aallam.openai.api.BetaOpenAI
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.wafflestudio.ai.icebreaker.application.icebreaking.IceBreakingService
import com.wafflestudio.ai.icebreaker.application.icebreaking.IceBreakingServiceV2
import com.wafflestudio.ai.icebreaker.application.icebreaking.textContent
import com.wafflestudio.ai.icebreaker.application.understanding.Understanding
import com.wafflestudio.ai.icebreaker.application.user.User
import com.wafflestudio.ai.icebreaker.application.user.UserInformation
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.asFlux
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.LocalDate

@RestController
@OptIn(BetaOpenAI::class)
@RequestMapping("/api/v1")
class TestController(
    private val iceBreakingServiceV2: IceBreakingServiceV2,
    private val iceBreakingService: IceBreakingService,
    private val objectMapper: ObjectMapper
) {
    @PostMapping("/test", produces = ["application/stream+json"])
    fun test(): Flux<Response> {
        val userA = User(
            1,
            "지혁",
            listOf(
                UserInformation.Gender.MALE,
                UserInformation.Birthday(LocalDate.of(2001, 3, 24).atStartOfDay()),
                UserInformation.Major("컴퓨터공학부")
            )
        )
        val userB = User(
            2,
            "지민",
            listOf(
                UserInformation.Gender.FEMALE,
                UserInformation.Birthday(LocalDate.of(2000, 4, 11).atStartOfDay()),
                UserInformation.UnderstandingInformation(Understanding.JOB, "카리나(본명 유지민)는 대한민국의 걸그룹 에스파 멤버이다.")
            )
        )

        return iceBreakingServiceV2.getIceBreakingQuestions(userA, userB, useGpt4 = true)
            .map {
                val stringResponse = it.textContent()
                if (iceBreakingService.isResultAnswer(stringResponse)) {
                    val result = iceBreakingService.extract(stringResponse)
                    Response.FinalResult(
                        runCatching {
                            objectMapper.readValue<Response.FinalResult>(result.single()).result
                        }.getOrElse {
                            IceBreakingService.logger.error { "Failed to parse result: $result" }
                            throw it
                        }
                    )
                } else {
                    val message = it.textContent()
                    Response.Thought(message)
                }
            }.asFlux()
    }

    interface Response {
        data class Thought(
            val thought: String
        ) : Response

        data class Question(
            val question: String,
            val keywords: List<String>
        )

        data class FinalResult(
            val result: List<Question>
        ) : Response
    }
}

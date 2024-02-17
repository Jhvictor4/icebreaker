package com.wafflestudio.ai.icebreaker

import com.aallam.openai.api.BetaOpenAI
import com.wafflestudio.ai.icebreaker.application.icebreaking.IceBreakingService
import com.wafflestudio.ai.icebreaker.application.icebreaking.IceBreakingServiceV2
import com.wafflestudio.ai.icebreaker.application.understanding.Understanding
import com.wafflestudio.ai.icebreaker.application.user.User
import com.wafflestudio.ai.icebreaker.application.user.UserInformation
import com.wafflestudio.ai.icebreaker.application.user.UserPort
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime

@OptIn(BetaOpenAI::class)
class IceBreakingServiceTest(
    private val iceBreakingService: IceBreakingService,
    private val iceBreakingServiceV2: IceBreakingServiceV2,
    private val userPort: UserPort
) {

    fun test(): Unit = runBlocking {
        val userA = User(
            1,
            "지혁",
            listOf(
                UserInformation.Gender.MALE,
                UserInformation(2001, 3, 24),
                UserInformation.Major("컴퓨터공학부")
            )
        )
        val userB = User(
            2,
            "지민",
            listOf(
                UserInformation.Gender.FEMALE,
                UserInformation(2000, 4, 11),
                UserInformation.UnderstandingInformation(Understanding.JOB, "카리나(본명 유지민)는 대한민국의 걸그룹 에스파 멤버이다.")
            )
        )

        val result = iceBreakingService.getIceBreakingQuestions(userA, userB, useGpt4 = true).result
        assert(result.size == 3)
    }

    fun test2(): Unit = runBlocking {
        val userA = User(
            1,
            "지혁",
            listOf(
                UserInformation.Gender.MALE,
                UserInformation(2001, 3, 24),
                UserInformation.Major("컴퓨터공학부")
            )
        )
        val userB = User(
            2,
            "지민",
            listOf(
                UserInformation.Gender.FEMALE,
                UserInformation(2000, 4, 11),
                UserInformation.UnderstandingInformation(Understanding.JOB, "카리나(본명 유지민)는 대한민국의 걸그룹 에스파 멤버이다.")
            )
        )

        val result = iceBreakingServiceV2.getIceBreakingQuestions(userA, userB, useGpt4 = true).toList()
        print(result)
    }

    fun test3(): Unit = runBlocking {
        val userA = userPort.getUser(1L)!!
        val userB = userPort.getUser(2L)!!

        val result = iceBreakingService.getIceBreakingQuestions(userA, userB, useGpt4 = true).result
        assert(result.size == 3)
    }

    private fun UserInformation(year: Int, month: Int, day: Int): UserInformation.Birthday {
        return UserInformation.Birthday(LocalDateTime.of(year, month, day, 0, 0))
    }
}

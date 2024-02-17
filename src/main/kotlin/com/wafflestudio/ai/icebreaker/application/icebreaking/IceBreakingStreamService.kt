package com.wafflestudio.ai.icebreaker.application.icebreaking

import com.aallam.openai.api.BetaOpenAI
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.wafflestudio.ai.icebreaker.api.ApplicationException
import com.wafflestudio.ai.icebreaker.application.meetup.MeetUp
import com.wafflestudio.ai.icebreaker.application.meetup.MeetUpId
import com.wafflestudio.ai.icebreaker.application.meetup.MeetUpStatus
import com.wafflestudio.ai.icebreaker.application.user.User
import com.wafflestudio.ai.icebreaker.outbound.meetup.MeetUpEntity
import com.wafflestudio.ai.icebreaker.outbound.meetup.MeetUpRepository
import com.wafflestudio.ai.icebreaker.outbound.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Component

typealias UserKey = String

@Component
class IceBreakingStreamService(
    private val meetUpRepository: MeetUpRepository,
    private val userRepository: UserRepository,
    private val iceBreakingServiceV2: IceBreakingServiceV2,
    private val iceBreakingService: IceBreakingService,
    private val objectMapper: ObjectMapper
) {
    private data class StrategyDecision(
        val meetUp: MeetUp,
        val userA: User,
        val userB: User,
        val strategy: StreamStrategy
    )

    private enum class StreamStrategy {
        LISTEN_TO_OPENAI,
        POLL_HISTORY
    }

    /**
     * Facade Pattern
     *
     * 1.
     */
    suspend fun getIceBreakingStream(meetUpId: MeetUpId): Flow<IceBreakingStreamResponse> {
        // select - and - update
        val decision = Lock.withLock(meetUpId.id) {
            val meetUp = meetUpRepository.findByMeetUpId(meetUpId.id)
                ?.toDomain()
                ?: throw ApplicationException.Common("MeetUp not found")

            val userA = userRepository.getUser(meetUp.userAId)
                ?: throw ApplicationException.Common("User not found")

            val userB = userRepository.getUser(meetUp.userBId)
                ?: throw ApplicationException.Common("User not found")

            val status = meetUp.status
            val strategy = when (status) {
                // CREATED -> analyze 업데이트 후 스트림 시작
                MeetUpStatus.CREATED -> {
                    meetUpRepository.save(MeetUpEntity.from(meetUp.copy(status = MeetUpStatus.ANALYZING)))
                    StreamStrategy.LISTEN_TO_OPENAI
                }
                MeetUpStatus.ANALYZING, MeetUpStatus.DONE -> {
                    StreamStrategy.POLL_HISTORY
                }
            }

            StrategyDecision(meetUp, userA, userB, strategy)
        }

        return when (decision.strategy) {
            StreamStrategy.LISTEN_TO_OPENAI -> initGptAndStream(decision.userA, decision.userB)
            StreamStrategy.POLL_HISTORY -> pollHistory(decision.meetUp, decision.userA, decision.userB)
        }
    }

    @OptIn(BetaOpenAI::class)
    private suspend fun initGptAndStream(userA: User, userB: User): Flow<IceBreakingStreamResponse> {
        return iceBreakingServiceV2.getIceBreakingQuestions(userA, userB)
            .map {
                val stringResponse = it.textContent()
                if (iceBreakingService.isResultAnswer(stringResponse)) {
                    val result = iceBreakingService.extract(stringResponse)
                    IceBreakingStreamResponse.FinalQuestion(
                        runCatching {
                            objectMapper.readValue<IceBreakingStreamResponse.FinalQuestion>(result.single()).result
                        }.getOrElse {
                            IceBreakingService.logger.error { "Failed to parse result: $result" }
                            throw it
                        }
                    )
                } else {
                    val message = it.textContent()
                    IceBreakingStreamResponse.Thought(message)
                }
            }
    }

    private fun pollHistory(meetUp: MeetUp, userA: User, userB: User): Flow<IceBreakingStreamResponse> {
        TODO("DB 상태 보면서 폴링")
    }
}

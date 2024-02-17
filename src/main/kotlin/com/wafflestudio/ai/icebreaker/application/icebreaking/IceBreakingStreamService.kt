package com.wafflestudio.ai.icebreaker.application.icebreaking

import com.aallam.openai.api.BetaOpenAI
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.wafflestudio.ai.icebreaker.api.ApplicationException
import com.wafflestudio.ai.icebreaker.application.FinalQuestions
import com.wafflestudio.ai.icebreaker.application.Log
import com.wafflestudio.ai.icebreaker.application.meetup.MeetUp
import com.wafflestudio.ai.icebreaker.application.meetup.MeetUpId
import com.wafflestudio.ai.icebreaker.application.meetup.MeetUpStatus
import com.wafflestudio.ai.icebreaker.application.user.User
import com.wafflestudio.ai.icebreaker.outbound.icebreaking.IceBreakingHistoryEntity
import com.wafflestudio.ai.icebreaker.outbound.icebreaking.IceBreakingHistoryRepository
import com.wafflestudio.ai.icebreaker.outbound.meetup.MeetUpEntity
import com.wafflestudio.ai.icebreaker.outbound.meetup.MeetUpRepository
import com.wafflestudio.ai.icebreaker.outbound.user.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import org.springframework.stereotype.Component
import kotlin.random.Random

typealias UserKey = String

@Component
class IceBreakingStreamService(
    private val meetUpRepository: MeetUpRepository,
    private val userRepository: UserRepository,
    private val iceBreakingServiceV2: IceBreakingServiceV2,
    private val iceBreakingService: IceBreakingService,
    private val iceBreakingHistoryRepository: IceBreakingHistoryRepository,
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

    suspend fun getIceBreakingStream(requestUserId: Long, meetUpId: MeetUpId): Flow<IceBreakingStreamResponse> {
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

//        logger.info { "[TEST] UserId: ${requestUserId}, MeetUpId: ${meetUpId.id} Selected strategy: ${decision.strategy}" }

        return when (decision.strategy) {
            StreamStrategy.LISTEN_TO_OPENAI -> initGptAndStream(decision.meetUp, decision.userA, decision.userB)
            StreamStrategy.POLL_HISTORY -> pollHistory(decision.meetUp)
        }
    }

    @OptIn(BetaOpenAI::class)
    private suspend fun initGptAndStream(meetUp: MeetUp, userA: User, userB: User): Flow<IceBreakingStreamResponse> {
        return iceBreakingServiceV2.getIceBreakingQuestions(userA, userB)
            .map {
                val stringResponse = it.textContent()
                if (iceBreakingService.isResultAnswer(stringResponse)) {
                    IceBreakingStreamResponse.FinalQuestion(
                        runCatching {
                            val cardNo = Random.nextInt(20) + 1
                            objectMapper.readValue<List<IceBreakingStreamResponse.Question>>(extract(stringResponse)).map { IceBreakingStreamResponse.Result(it.question, it.keywords, cardNo) }
                        }.getOrElse {
                            IceBreakingService.logger.error { "Failed to parse result: $stringResponse" }
                            throw it
                        }
                    )
                } else {
                    val message = it.textContent()
                    IceBreakingStreamResponse.Thought(message)
                }.also {
                    iceBreakingHistoryRepository.save(IceBreakingHistoryEntity.from(IceBreakingHistory.from(meetUp.meetUpId, it)))
                }
            }
            .onCompletion {
                meetUpRepository.save(MeetUpEntity.from(meetUp.copy(status = MeetUpStatus.DONE)))
            }
    }

    private fun extract(input: String): String {
        val regex = Regex("(?s)<RESPONSE>(.*?)</RESPONSE>")
        val matches = regex.findAll(input)
        return matches.map { it.groupValues[1].trim() }
            .firstOrNull()
            .orEmpty()
    }

    private fun pollHistory(meetUp: MeetUp): Flow<IceBreakingStreamResponse> = flow {
        val meetUpId = meetUp.meetUpId
        var updatedMeetUp = meetUpRepository.findByMeetUpId(meetUpId)
            ?.toDomain()
            ?: throw ApplicationException.Common("MeetUp not found")

        var cursor = 0L
        do {
            val newHistories = iceBreakingHistoryRepository
                .findAllByMeetUpIdAndIdGreaterThanOrderById(meetUpId, cursor)
                .map { it.toDomain() }

            if (newHistories.isNotEmpty()) {
                emitAll(newHistories.asFlow().map { it.toResponse() })
                cursor += newHistories.maxOf { it.id }
            }

            updatedMeetUp = meetUpRepository.findByMeetUpId(updatedMeetUp.meetUpId)
                ?.toDomain()
                ?: throw ApplicationException.Common("MeetUp not found")
        } while (updatedMeetUp.status != MeetUpStatus.DONE)

        // try more after delaying 100ms
        delay(200)
        val newHistories = iceBreakingHistoryRepository
            .findAllByMeetUpIdAndIdGreaterThanOrderById(meetUpId, cursor)
            .map { it.toDomain() }

        if (newHistories.isNotEmpty()) {
            emitAll(newHistories.asFlow().map { it.toResponse() })
        }
    }

    companion object: Log
}

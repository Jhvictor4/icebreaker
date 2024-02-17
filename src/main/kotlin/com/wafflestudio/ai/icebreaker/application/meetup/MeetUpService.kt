package com.wafflestudio.ai.icebreaker.application.meetup

import com.wafflestudio.ai.icebreaker.application.user.User
import com.wafflestudio.ai.icebreaker.outbound.meetup.MeetUpEntity
import com.wafflestudio.ai.icebreaker.outbound.meetup.MeetUpRepository
import org.springframework.stereotype.Component

@Component
class MeetUpService(
    private val meetUpRepository: MeetUpRepository
) {
    suspend fun myMeetUpStatus(userId: Long, meetUpId: String): MeetUp? {
        return meetUpRepository.findByMeetUpId(meetUpId)?.toDomain()
    }

    suspend fun joinMeetUpUrl(user: User, meetUpId: String): MeetUp {
        val findRequestedUserId = try {
            MeetUpId(meetUpId).extractUserId()
        } catch (e: Exception) {
            throw IllegalStateException("Invalid meetUpId")
        }

        val meetUp = MeetUp.create(
            meetUpId = meetUpId,
            requestedUserId = findRequestedUserId,
            joinedUserId = user.id,
            status = MeetUpStatus.CREATED
        )

        return meetUpRepository.save(MeetUpEntity.from(meetUp)).toDomain()
    }
}

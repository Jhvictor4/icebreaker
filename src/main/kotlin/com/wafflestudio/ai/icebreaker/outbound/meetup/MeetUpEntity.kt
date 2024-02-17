package com.wafflestudio.ai.icebreaker.outbound.meetup

import com.wafflestudio.ai.icebreaker.application.meetup.MeetUp
import com.wafflestudio.ai.icebreaker.application.meetup.MeetUpStatus
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

@Table("meet_up")
data class MeetUpEntity(
    @Id
    val id: Long,
    @Column("meet_up_id")
    val meetUpId: String,
    @Column("user_key")
    val userKey: String,
    @Column("requested_user_id")
    val requestedUserId: Long,
    @Column("joined_user_id")
    val joinedUserId: Long,
    @Column("status")
    val status: String
) {
    fun toDomain(): MeetUp {
        return MeetUp(
            id = id,
            meetUpId = meetUpId,
            requestedUserId = requestedUserId,
            joinedUserId = joinedUserId,
            status = MeetUpStatus.valueOf(status)
        )
    }

    companion object {
        fun from(meetUp: MeetUp): MeetUpEntity {
            return MeetUpEntity(
                id = meetUp.id,
                meetUpId = meetUp.meetUpId,
                userKey = meetUp.userKey,
                requestedUserId = meetUp.requestedUserId,
                joinedUserId = meetUp.joinedUserId,
                status = meetUp.status.name
            )
        }
    }
}

interface MeetUpRepository : CoroutineCrudRepository<MeetUpEntity, Long> {
    suspend fun findByMeetUpId(meetUpId: String): MeetUpEntity?
}

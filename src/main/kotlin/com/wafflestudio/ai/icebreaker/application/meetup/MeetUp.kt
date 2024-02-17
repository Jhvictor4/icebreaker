package com.wafflestudio.ai.icebreaker.application.meetup

data class MeetUpId(
    val id: String
) {
    fun extractUserId(): Long {
        return id.split("::")[1].toLong()
    }
}

enum class MeetUpRequestStatus {
    NONE,
    WAITING
}

data class MeetUpRequestStatusDto(
    val status: MeetUpRequestStatus,
    val meetUpId: String?
)

// 생성된 meetUp Room
data class MeetUp(
    val id: Long,
    val meetUpId: String,
    val requestedUserId: Long,
    val joinedUserId: Long,
    val status: MeetUpStatus
) {
    private val userIdOrdered =
        listOf(requestedUserId, joinedUserId).sorted()

    val userKey: String get() {
        return userIdOrdered.joinToString("_")
    }

    val userAId get() = userIdOrdered.first()
    val userBId get() = userIdOrdered.last()

    companion object {
        fun create(
            meetUpId: String,
            requestedUserId: Long,
            joinedUserId: Long,
            status: MeetUpStatus
        ): MeetUp {
            return MeetUp(
                id = 0,
                meetUpId = meetUpId,
                requestedUserId = requestedUserId,
                joinedUserId = joinedUserId,
                status = status
            )
        }
    }
}

enum class MeetUpStatus {
    CREATED,
    ANALYZING,
    DONE
}

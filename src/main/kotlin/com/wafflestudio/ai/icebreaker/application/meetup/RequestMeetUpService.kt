package com.wafflestudio.ai.icebreaker.application.meetup

import com.wafflestudio.ai.icebreaker.application.LocalCache
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class RequestMeetUpService(cacheManager: CacheManager) {

    private val BASE_URL = "https://icebreaker.wafflestudio.com"
    private val meetUpRequestCache = cacheManager.getCache(LocalCache.MEET_UP_REQUEST_CACHE.alias)
        ?: throw IllegalStateException("Cache ${LocalCache.MEET_UP_REQUEST_CACHE.alias} not found")

    private data class MeetUpRequestCacheData(
        val meetUpId: MeetUpId,
        val requestedUserId: Long // 필요없긴함
    )

    fun requestMeetUp(
        userId: Long
    ): MeetUpId {
        val meetUpId = MeetUpId("${UUID.randomUUID()}::$userId")
        if (meetUpRequestCache.putIfAbsent(userId, MeetUpRequestCacheData(meetUpId, userId)) != null) {
            throw IllegalStateException("MeetUp request already exists for user $userId")
        }

        return meetUpId
    }

    fun createMeetUpUrl(userId: Long, meetUpId: MeetUpId): String {
        // expire check
        val meetUpQR = meetUpRequestCache.get(meetUpId, MeetUpRequestCacheData::class.java)
            ?: throw IllegalStateException("MeetUp request not found for meetUpId $meetUpId")

        // validation
        check(meetUpQR.meetUpId.id == meetUpId.id) { "MeetUp request not found for meetUpId $meetUpId" }
        check(meetUpQR.requestedUserId == userId) { "MeetUp request not found for meetUpId $meetUpId" }

        return "$BASE_URL/meet/$meetUpId"
    }

    fun myMeetUpRequestStatus(userId: Long): MeetUpRequestStatusDto {
        val cachedEntry = meetUpRequestCache.get(userId, MeetUpRequestCacheData::class.java)
        return if (cachedEntry == null) {
            // expired or not requested
            MeetUpRequestStatusDto(MeetUpRequestStatus.NONE, null)
        } else {
            // has waiting meet-up request. check if it's created
            MeetUpRequestStatusDto(MeetUpRequestStatus.WAITING, cachedEntry.meetUpId)
        }
    }
}

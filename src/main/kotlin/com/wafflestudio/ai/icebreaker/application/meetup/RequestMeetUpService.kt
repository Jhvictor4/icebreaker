package com.wafflestudio.ai.icebreaker.application.meetup

import com.wafflestudio.ai.icebreaker.api.ApplicationException
import com.wafflestudio.ai.icebreaker.application.LocalCache
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class RequestMeetUpService(cacheManager: CacheManager) {

    private val BASE_URL = "https://icebreaker.wafflestudio.com"
    private val meetUpRequestCache = cacheManager.getCache(LocalCache.MEET_UP_REQUEST_CACHE.alias)
        ?: throw ApplicationException.Common("Cache ${LocalCache.MEET_UP_REQUEST_CACHE.alias} not found")

    private data class MeetUpRequestCacheData(val meetUpId: String)

    fun requestMeetUp(
        userId: Long
    ): MeetUpId {
        val meetUpId = MeetUpId("${UUID.randomUUID()}::$userId")
        if (meetUpRequestCache.putIfAbsent(userId, MeetUpRequestCacheData(meetUpId.id)) != null) {
            throw ApplicationException.Common("MeetUp request already exists for user $userId")
        }

        return meetUpId
    }

    fun createMeetUpUrl(userId: Long, meetUpId: MeetUpId): String {
        // expire check
        val meetUpQR = meetUpRequestCache.get(userId, MeetUpRequestCacheData::class.java)
            ?: throw ApplicationException.Common("MeetUp request not found for user $userId")

        // validation
        check(meetUpQR.meetUpId == meetUpId.id) { "MeetUp request not found for meetUpId $meetUpId" }

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

    fun evictMeetUpId(userId: Long) {
        meetUpRequestCache.evict(userId)
    }
}

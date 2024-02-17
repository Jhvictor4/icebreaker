package com.wafflestudio.ai.icebreaker.application

import java.time.Duration

enum class LocalCache(val alias: String, val expireAfterWrite: Duration) {
    MEET_UP_REQUEST_CACHE("iceBreakingCache", Duration.ofDays(3))
}

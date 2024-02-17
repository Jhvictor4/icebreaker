package com.wafflestudio.ai.icebreaker.application.icebreaking

import com.wafflestudio.ai.icebreaker.application.Log
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.yield
import java.util.concurrent.ConcurrentHashMap

object Lock : Log {
    private val lockMap = ConcurrentHashMap<UserKey, Boolean>()

    fun release(key: String) {
        lockMap.remove(key)
    }

    fun <T : Any?> withSynchronousLock(key: String, func: () -> T): T {
        do {
            val lock = lockMap.putIfAbsent(key, true)
        } while (lock != null)

        logger.debug { "locked $key" }
        val result = func.invoke()
        release(key)
        logger.debug { "released $key with result $result" }
        return result
    }

    suspend fun <T : Any?> withLock(key: String, func: suspend () -> T): T = coroutineScope {
        while (lockMap.putIfAbsent(key, true) != null) {
            yield()
        }

        logger.debug { "locked $key" }
        val result = func.invoke()
        release(key)
        logger.debug { "released $key with result $result" }
        result
    }
}

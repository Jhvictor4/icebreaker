package com.wafflestudio.ai.icebreaker.outbound.configuration

import com.github.benmanes.caffeine.cache.Caffeine
import com.wafflestudio.ai.icebreaker.application.LocalCache
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    fun cacheManager(): CacheManager {
        return SimpleCacheManager().apply {
            val caches = LocalCache.entries.map {
                CaffeineCache(
                    it.alias,
                    Caffeine.newBuilder()
                        .expireAfterWrite(it.expireAfterWrite.seconds, TimeUnit.SECONDS)
                        .maximumSize(100)
                        .build()
                )
            }

            setCaches(caches)
        }
    }
}

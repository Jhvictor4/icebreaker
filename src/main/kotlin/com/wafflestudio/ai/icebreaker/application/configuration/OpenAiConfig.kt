package com.wafflestudio.ai.icebreaker.application.configuration

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.RetryStrategy
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.logging.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration.Companion.minutes

@Configuration
internal class OpenAiConfig(
    @Value("\${openai.api-key}")
    private val apiKey: String
) {

    @Bean
    fun openAiClient(): OpenAI {
        return OpenAI(
            token = apiKey,
            timeout = Timeout(socket = 1.minutes),
            retry = RetryStrategy(maxRetries = 0),
            httpClientConfig = {
                followRedirects = true
                install(Logging) {
                    level = LogLevel.NONE // 로깅 비활성화. 너무 시끄러움..
                }
            }
        )
    }
}

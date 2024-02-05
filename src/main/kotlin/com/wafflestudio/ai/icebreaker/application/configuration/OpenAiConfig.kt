package com.wafflestudio.ai.icebreaker.application.configuration

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.RetryStrategy
import io.ktor.client.engine.cio.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager
import kotlin.time.Duration.Companion.seconds

@Configuration
internal class OpenAiConfig(
    @Value("\${openai.api-key}")
    private val apiKey: String
) {

    @Bean
    fun openAiClient(): OpenAI {
        return OpenAI(
            token = apiKey,
            timeout = Timeout(socket = 10.seconds),
            retry = RetryStrategy(maxRetries = 0),
            httpClientConfig = {
                followRedirects = true
                engine {
                    this as CIOEngineConfig
                    https {
                        trustManager = object: X509TrustManager {
                            override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) { }

                            override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) { }

                            override fun getAcceptedIssuers(): Array<X509Certificate>? = null
                        }
                    }
                }
            }
        )
    }

}
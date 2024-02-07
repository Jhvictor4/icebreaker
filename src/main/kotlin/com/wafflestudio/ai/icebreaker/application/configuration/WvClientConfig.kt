package com.wafflestudio.ai.icebreaker.application.configuration

import com.wafflestudio.ai.icebreaker.application.Log
import io.weaviate.client.Config
import io.weaviate.client.WeaviateAuthClient
import io.weaviate.client.WeaviateClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WvClientConfig(
    @Value("\${weaviate.host}") private val host: String,
    @Value("\${weaviate.scheme}") private val scheme: String,
    @Value("\${weaviate.api-key}") private val apiKey: String
) {

    @Bean
    fun wvClient(): WeaviateClient {
        return WeaviateAuthClient.apiKey(Config(scheme, host), apiKey).also {
            val meta = it.misc().metaGetter().run()
            if (meta.error == null) {
                logger.info { "Weaviate client is successfully initialized | ${meta.result.version}" }
            } else {
                throw RuntimeException("Weaviate client is failed to initialize | ${meta.error.messages}")
            }
        }
    }

    companion object : Log
}

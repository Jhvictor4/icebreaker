package com.wafflestudio.ai.icebreaker.application.configuration

import com.wafflestudio.ai.icebreaker.api.ApplicationException
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
    @Value("\${weaviate.api-key}") private val apiKey: String,
    @Value("\${openai.api-key}") private val openAiApiKey: String,
) {

    @Bean
    fun wvClient(): WeaviateClient {
        val headers = mapOf(
            "X-OpenAI-Api-Key" to openAiApiKey
        )
        return WeaviateAuthClient.apiKey(Config(scheme, host, headers), apiKey).also {
            val meta = it.misc().metaGetter().run()
            if (meta.error == null) {
                logger.info { "Weaviate client is successfully initialized | ${meta.result.version}" }
            } else {
                throw ApplicationException.Common("Weaviate client is failed to initialize | ${meta.error.messages}")
            }
        }
    }

    companion object : Log
}

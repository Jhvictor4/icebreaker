package com.wafflestudio.ai.icebreaker.outbound.understanding

import com.wafflestudio.ai.icebreaker.application.common.ChatGptMessageResponseDto
import com.wafflestudio.ai.icebreaker.application.common.ChatGptPort
import com.wafflestudio.ai.icebreaker.application.configuration.coroutineContext
import com.wafflestudio.ai.icebreaker.application.understanding.UnderstandingUseCase
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component

@Component
class UnderstandingUseCaseAdapter(
    private val client: ChatGptPort
) : UnderstandingUseCase {

    override fun understandByUri(uri: String): ChatGptMessageResponseDto? {
        return runBlocking(coroutineContext) {
            client.createChat(
                """
            This is an understandable uri of certain person.
            summarize the person's characteristics and life experiences after browsing the url.
            uri: $uri
                """.trimIndent()
            )
        }
    }
}

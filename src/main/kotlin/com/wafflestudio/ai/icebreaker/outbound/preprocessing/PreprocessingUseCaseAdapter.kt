package com.wafflestudio.ai.icebreaker.outbound.preprocessing

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ImagePart
import com.aallam.openai.api.core.Role
import com.wafflestudio.ai.icebreaker.application.common.ChatGptPort
import com.wafflestudio.ai.icebreaker.application.common.ChatGptResponseDto
import com.wafflestudio.ai.icebreaker.application.configuration.coroutineContext
import com.wafflestudio.ai.icebreaker.application.preprocessing.PreprocessingUseCase
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component

@Component
class PreprocessingUseCaseAdapter(
    private val client: ChatGptPort
) : PreprocessingUseCase {

    override fun summarizeImages(images: List<String>): ChatGptResponseDto? {
        return runBlocking(coroutineContext) {
            client.createChat(
                prompt = """
            This is an images related to a certain person.
            summarize the features and content of the images.
            
            When you make suggestions, you should consider the following:
            - Do not just repeat example.
            - Mostly Korean text in the image, please refer to it
            - For images of animals or objects, it's likely the user's preferred subjects
            - When looking at an image that appears to be a timetable, please provides a summary of the class names in korean
            - Do not interrupt until the end.
                """.trimIndent(),
                conversations = listOf(ChatMessage(Role.User, images.map {
                    ImagePart("data:image/jpg;base64,${it}", "low")
                })),
                specificModel = "gpt-4-vision-preview",
                maxToken = 300,
            )
        }
    }

}
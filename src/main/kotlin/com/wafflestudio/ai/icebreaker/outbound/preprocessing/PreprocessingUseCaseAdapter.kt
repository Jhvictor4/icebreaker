package com.wafflestudio.ai.icebreaker.outbound.preprocessing

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ImagePart
import com.aallam.openai.api.core.Role
import com.wafflestudio.ai.icebreaker.application.Log
import com.wafflestudio.ai.icebreaker.application.common.ChatGptPort
import com.wafflestudio.ai.icebreaker.application.common.ChatGptResponseDto
import com.wafflestudio.ai.icebreaker.application.preprocessing.PreprocessingUseCase
import com.wafflestudio.ai.icebreaker.application.storage.getFileExtensionValue
import com.wafflestudio.ai.icebreaker.application.storage.port.StorageUseCase
import com.wafflestudio.ai.icebreaker.application.user.UserInformation
import com.wafflestudio.ai.icebreaker.outbound.user.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

@Component
class PreprocessingUseCaseAdapter(
    private val client: ChatGptPort,
    private val userRepository: UserRepository,
    private val storageUseCase: StorageUseCase
) : PreprocessingUseCase {

    override fun summarizeImages(id: Long, images: List<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            val user = userRepository.getUser(id)!!
            val response = client.createChat(
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
                conversations = listOf(
                    ChatMessage(
                        Role.User,
                        images.map { filename ->
                            val bytes = Files.readAllBytes(Path.of("${storageUseCase.getRoot()}/$id/$filename"))
                            val file = Base64.getEncoder().encodeToString(bytes)
                            val extensionValue = getFileExtensionValue(filename)
                            ImagePart("data:$extensionValue;base64,$file", "low")
                        }
                    )
                ),
                specificModel = "gpt-4-vision-preview",
                maxToken = 300
            )
            // response 저장.
            val summaryText = when (response) {
                is ChatGptResponseDto.Message -> response.message
                else -> ""
            }
            val newUserInfo = user.information
                .filter { it !is UserInformation.ImageSummary && it !is UserInformation.ImageUrl }
                .toMutableList()

            newUserInfo.add(UserInformation.ImageUrl(images))
            newUserInfo.add(UserInformation.ImageSummary(summaryText))
            user.information = newUserInfo
            userRepository.create(user)
            logger.info { "insert summaryText" }
        }
    }

    companion object : Log
}

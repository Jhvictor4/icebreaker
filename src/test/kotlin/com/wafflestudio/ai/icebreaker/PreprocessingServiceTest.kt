package com.wafflestudio.ai.icebreaker

import com.wafflestudio.ai.icebreaker.application.preprocessing.PreprocessingUseCase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class PreprocessingServiceTest @Autowired constructor(
    val preprocessingUseCase: PreprocessingUseCase
) {

    fun summarizeImage() {
        val paths = listOf("cat_for_query.jpg", "human_for_query.jpg", "timetable.jpg")

        val strings = paths.map {
            val resource = ClassPathResource(it)
            val bytes = Files.readAllBytes(Path.of(resource.uri))
            Base64.getEncoder().encodeToString(bytes)
        }

        println(preprocessingUseCase.summarizeImages(1, strings))
    }
}

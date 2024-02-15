package com.wafflestudio.ai.icebreaker.application.user

import com.wafflestudio.ai.icebreaker.application.understanding.Understanding
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Repository
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.util.*

@Repository
class UserRepository : UserPort {

    val users = listOf(
        User(
            1L,
            "지혁",
            listOf(
                UserInformation.Gender.MALE,
                UserInformation(2001, 3, 24),
                UserInformation.Major("컴퓨터공학부"),
            ),
            imageSummaryText = """
           The first image is of a cute, light beige or orange kitten with a soft, fluffy coat. Its eyes are a little wide, giving it an innocent and curious expression. The kitten is sitting against a clean, white background, which highlights its features and fur color.

The second image features a smiling young man wearing a denim shirt. He seems cheerful and is pointing to his right side with both hands, possibly indicating something or suggesting a direction. The man is standing against a plain white background, which keeps the focus entirely on him and his gestures.

The third image appears to be a class timetable with a colorful design, showing different classes scheduled at various times throughout a single day. The classes are written in Korean, and here is a summary of the class names along with their corresponding times:

- 11:00 - 자료구조및실습 (Data Structures and Practice)
- 12:00 - 자료구조및실습 (Data Structures and Practice)
- 14:00 - 대화 (Dialogue)
- 15:00 - 스크립트언어 (Script Language)
- 16:00 - 컴퓨터구조 (Computer Architecture)
        """.trimIndent()
        ),
        User(
            2L,
            "지민",
            listOf(
                UserInformation.Gender.FEMALE,
                UserInformation(2000, 4, 11),
                UserInformation.UnderstandingInformation(Understanding.JOB, "카리나(본명 유지민)는 대한민국의 걸그룹 에스파 멤버이다.")
            ),
            imageSummaryText = """
The first image is of a domestic cat lying down on a soft surface. The cat has a striking pattern with grey and black stripes over its back, merging into a predominantly white chest and paws. Its eyes are attentively fixed on something in the distance, giving it an alert and curious expression.

The second image features a young woman facing the camera with a neutral expression on her face. She has a clear complexion and dark hair that is pulled back from her face, highlighting her features. She wears a simple white sleeveless top.
  """.trimIndent()
        )
    )

    val imagePaths = mapOf(
        1L to listOf("timetable.jpg", "cat.jpg", "human.jpg"),
        2L to listOf("cat_for_query.jpg", "human_for_query.jpg")
    )

    override fun getUser(id: Long): User? {
        val map = users.associateBy { it.id }
        return map[id]?.apply {
            images = getImages(id)
        }
    }

    private fun getImages(id: Long): List<String> {
        val imagePaths = imagePaths[id]

        val images = imagePaths?.map {
            val resource = ClassPathResource(it)
            val bytes = Files.readAllBytes(Path.of(resource.uri))
            Base64.getEncoder().encodeToString(bytes)
        }
        return images ?: emptyList()
    }

    private fun UserInformation(year: Int, month: Int, day: Int): UserInformation.Birthday {
        return UserInformation.Birthday(LocalDateTime.of(year, month, day, 0, 0))
    }

}
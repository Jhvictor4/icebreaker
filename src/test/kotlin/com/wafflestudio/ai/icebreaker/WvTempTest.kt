package com.wafflestudio.ai.icebreaker

import com.wafflestudio.ai.icebreaker.application.common.WeaviatePort
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import java.nio.file.Files
import java.nio.file.Path

/**
 * Weaviate 작동 여부 테스트.
 */
@SpringBootTest
class WvTempTest @Autowired constructor(
    val weaviatePort: WeaviatePort,
) {

    @Test
    fun `스키마 추가`() {
        weaviatePort.addSchema("Question")
    }

    @Test
    fun `스키마 조회`() {
        weaviatePort.getSchema()
    }

    @Test
    fun `단일 데이터 저장`() {
        weaviatePort.save("Question", "kitty")
        weaviatePort.save("Question", "dog")
        weaviatePort.save("Question", "puppy")
        weaviatePort.save("Question", "world")
        weaviatePort.save("Question", "sea")
        weaviatePort.save("Question", "good")
    }

    @Test
    fun `이미지 데이터 저장`() {
        val paths = listOf("cat.jpg", "human.jpg")

        for (path in paths) {
            val resource = ClassPathResource(path)
            val bytes = Files.readAllBytes(Path.of(resource.uri))
            weaviatePort.saveImage("Question", bytes)
        }
    }

    @Test
    fun `데이터 조회`() {
        weaviatePort.getAll("Question")
    }

    @Test
    fun `cat 텍스트로 유사한 데이터 조회`() {
        weaviatePort.searchNearText("Question", "cat")
    }

    @Test
    fun `human 텍스트로 유사한 데이터 조회`() {
        weaviatePort.searchNearText("Question", "people")
    }

    @Test
    fun `고양이 이미지 유사한 데이터 조회`() {
        val path = "cat_for_query.jpg"
        val resource = ClassPathResource(path)
        val bytes = Files.readAllBytes(Path.of(resource.uri))
        weaviatePort.searchNearImage("Question", bytes)
    }

    @Test
    fun `사람 이미지로 유사한 데이터 조회`() {
        val path = "human_for_query.jpg"
        val resource = ClassPathResource(path)
        val bytes = Files.readAllBytes(Path.of(resource.uri))
        weaviatePort.searchNearImage("Question", bytes)
    }

}
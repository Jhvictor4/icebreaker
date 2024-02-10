package com.wafflestudio.ai.icebreaker

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.wafflestudio.ai.icebreaker.application.outbound.Question
import com.wafflestudio.ai.icebreaker.application.outbound.WeaviatePort
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.net.URI

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
    fun `데이터 저장`() {
        val json = URI("https://raw.githubusercontent.com/weaviate-tutorials/quickstart/main/data/jeopardy_tiny.json").toURL().readText()
        weaviatePort.save("Question", jacksonObjectMapper().readValue<List<Question>>(json))
    }

    @Test
    fun `유사 텍스트 조회`() {
        weaviatePort.nearTextQuery("Question", "biology")
    }

}
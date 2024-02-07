package com.wafflestudio.ai.icebreaker.application.outbound

import com.fasterxml.jackson.annotation.JsonProperty
import com.wafflestudio.ai.icebreaker.application.Log
import io.weaviate.client.WeaviateClient
import io.weaviate.client.v1.data.model.WeaviateObject
import io.weaviate.client.v1.graphql.query.argument.NearTextArgument
import io.weaviate.client.v1.graphql.query.fields.Field
import io.weaviate.client.v1.schema.model.Schema
import io.weaviate.client.v1.schema.model.WeaviateClass
import org.springframework.stereotype.Component

interface WeaviatePort {

    fun addSchema(className: String)

    fun getSchema(): Schema

    fun save(className: String, data: List<Question>)

    fun nearTextQuery(className: String, text: String)

}

@Component
class WeaviateAdapter(
    private val wvClient: WeaviateClient,
) : WeaviatePort {

    override fun addSchema(className: String) {
        val classObj = WeaviateClass.builder()
            .className(className)
            .vectorizer("text2vec-openai")
            .moduleConfig(mapOf(
                "text2vec-openai" to emptyMap<String, Any?>(),
                "generative-openai" to emptyMap<String, Any?>()
            ))
            .build()
        val res = wvClient.schema().classCreator().withClass(classObj).run()
        if (res.error == null) {
            logger.info { "Weaviate schema creation successful!" }
        } else {
            throw RuntimeException("Weaviate schema creation failed! | ${res.error.messages}")
        }
    }

    override fun getSchema(): Schema {
        val schema = wvClient.schema().getter().run().result
        logger.info { schema }
        return schema
    }

    // TODO: 일반화 필요.
    override fun save(className: String, data: List<Question>) {
        var batcher = wvClient.batch().objectsBatcher()
        for (question in data) {
            val wvObject = WeaviateObject.builder()
                .className(className)
                .properties(mapOf(
                    "answer" to question.answer,
                    "question" to question.question,
                    "category" to question.category,
                ))
                .build()
            batcher = batcher.withObjects(wvObject)
        }
        val res = batcher.run()
        if (res.error == null) {
            logger.info { "Weaviate save successful!" }
        } else {
            throw RuntimeException("Weaviate save failed! | ${res.error.messages}")
        }
    }

    // TODO: 일반화 필요
    override fun nearTextQuery(className: String, text: String) {
        val res = wvClient.graphQL()
            .get()
            .withClassName(className)
            .withFields(Field.builder().name("question answer category").build())
            .withNearText(NearTextArgument.builder().concepts(arrayOf(text)).build())
            .withLimit(2)
            .run()
        if (res.error == null) {
            logger.info { "Weaviate successfully executed a nearby text query! | ${res.result}" }
        } else {
            throw RuntimeException("Weaviate failed to executed a nearby text query! | ${res.error.messages}")
        }
    }

    companion object : Log

}

data class Question(
    @JsonProperty("Answer")
    val answer: String,
    @JsonProperty("Question")
    val question: String,
    @JsonProperty("Category")
    val category: String,
)

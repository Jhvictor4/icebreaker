package com.wafflestudio.ai.icebreaker.outbound.common

import com.fasterxml.jackson.annotation.JsonProperty
import com.wafflestudio.ai.icebreaker.application.Log
import com.wafflestudio.ai.icebreaker.application.common.WeaviatePort
import io.weaviate.client.WeaviateClient
import io.weaviate.client.v1.data.replication.model.ConsistencyLevel
import io.weaviate.client.v1.graphql.query.argument.NearTextArgument
import io.weaviate.client.v1.graphql.query.fields.Field
import io.weaviate.client.v1.schema.model.Schema
import io.weaviate.client.v1.schema.model.WeaviateClass
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class WeaviateAdapter(
    private val wvClient: WeaviateClient
) : WeaviatePort {

    override fun addSchema(className: String) {
        val classObj = WeaviateClass.builder()
            .className(className)
            .vectorizer("text2vec-openai")
            .moduleConfig(
                mapOf(
                    "text2vec-openai" to emptyMap<String, Any?>(),
                    "generative-openai" to emptyMap<String, Any?>()
                )
            )
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

    override fun save(className: String, id: Int, description: String) {
        val data = mapOf(
            "cardId" to id.toString(),
            "description" to description,
        )

        val result = wvClient.data().creator()
            .withClassName(className)
            .withProperties(data)
            .withConsistencyLevel(ConsistencyLevel.ALL)
            .run()

        if (result.hasErrors()) {
            System.out.println(result.getError());
            return;
        }
        System.out.println(result.getResult());
    }

    override fun getAll(className: String) {
        val result = wvClient.data().objectsGetter()
            .withClassName(className)
            .withAdditional("classification")
            .run()

        if (result.hasErrors()) {
            println(result.getError());
            return;
        }
        println(result.getResult());
    }

    // TODO: 일반화 필요
    override fun nearTextQuery(className: String, keywords: List<String>): Int? {
        try {
            val res = wvClient.graphQL()
                .get()
                .withClassName(className)
                .withFields(Field.builder().name("description").build(), Field.builder().name("cardId").build())
                .withNearText(NearTextArgument.builder().concepts(keywords.toTypedArray()).build())
                .withLimit(1)
                .run()
            if (res.error == null) {
                val output = extractValue(res.result.data.toString(), "cardId")
                logger.info { "Weaviate successfully executed a nearby text query! | ${output} | ${res.result}" }
                return output?.toIntOrNull()
            } else {
                logger.info { "Weaviate failed to executed a nearby text query! | ${res.error.messages}" }
                return null
            }
        } catch (e: Exception) {
            return null
        }
    }

    companion object : Log
}

data class Card(
    val id: Int,
    val description: String,
)

fun extractValue(input: String, key: String): String? {
    val pattern = Pattern.compile("$key=([^,\\\\}]*)")
    val matcher = pattern.matcher(input)
    if (!matcher.find()) {
        return null
    }
    return matcher.group(1).trim()
}


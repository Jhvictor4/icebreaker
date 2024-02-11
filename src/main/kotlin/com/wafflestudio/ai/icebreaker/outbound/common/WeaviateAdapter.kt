package com.wafflestudio.ai.icebreaker.outbound.common

import com.wafflestudio.ai.icebreaker.application.Log
import com.wafflestudio.ai.icebreaker.application.common.WeaviatePort
import io.weaviate.client.WeaviateClient
import io.weaviate.client.v1.data.replication.model.ConsistencyLevel
import io.weaviate.client.v1.graphql.query.argument.NearTextArgument
import io.weaviate.client.v1.graphql.query.fields.Field
import io.weaviate.client.v1.schema.model.Property
import io.weaviate.client.v1.schema.model.Schema
import io.weaviate.client.v1.schema.model.WeaviateClass
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.io.File
import java.util.*
import java.util.regex.Pattern
import javax.imageio.ImageIO

@Component
class WeaviateAdapter(
    private val wvClient: WeaviateClient,
) : WeaviatePort {

    override fun addSchema(className: String) {
        val classObj = WeaviateClass.builder()
            .className(className)
            .vectorizer("multi2vec-clip")
            .moduleConfig(
                mapOf(
                    "multi2vec-clip" to mapOf(
                        "textFields" to listOf("name"),
                        "imageFields" to listOf("image"),
                        "weights" to mapOf(
                            "textFields" to listOf(0.5),
                            "imageFields" to listOf(0.5),
                        )
                    ),
                )
            )
            .properties(
                listOf(
                    Property.builder()
                        .dataType(listOf("text"))
                        .name("name")
                        .build(),
                    Property.builder()
                        .dataType(listOf("blob"))
                        .name("image")
                        .build(),
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

    override fun save(className: String, text: String) {
        val data = mapOf(
            "name" to text
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

    override fun saveImage(className: String, fileContent: ByteArray) {
        val imageBase64 = Base64.getEncoder().encodeToString(fileContent)
        val data = mapOf(
            "image" to imageBase64
        )

        val result = wvClient.data().creator()
            .withClassName(className)
            .withProperties(data)
            .withConsistencyLevel(ConsistencyLevel.ALL)
            .run()

        if (result.hasErrors()) {
            System.err.println(result.getError());
            return;
        }
        println(result.getResult());
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

    override fun searchNearText(className: String, text: String) {
        val res = wvClient.graphQL()
            .get()
            .withClassName(className)
            .withFields(Field.builder().name("name").build(), Field.builder().name("image").build())
            .withNearText(NearTextArgument.builder().concepts(arrayOf(text)).build())
            .withLimit(1)
            .run()
        if (res.error == null) {
            val input = res.result.data.toString()
            val image = extractValue(input, "image")
            val name = extractValue(input, "name")
            if (image != null && image != "null") {
                val decoded = Base64.getDecoder().decode(image)
                printImage(decoded)
            }
            if (name != null && name != "null") {
                println(name)
            }
            logger.info { "Weaviate successfully executed a nearby text query!" }
        } else {
            throw RuntimeException("Weaviate failed to executed a nearby text query! | ${res.error.messages}")
        }
    }

    override fun searchNearImage(className: String, fileContent: ByteArray) {
        val imageBase64 = Base64.getEncoder().encodeToString(fileContent)
        val nearImage = wvClient.graphQL().arguments().nearImageArgBuilder()
            .image(imageBase64)
            .build()

        val result = wvClient.graphQL().get()
            .withClassName(className)
            .withFields(Field.builder().name("image").build(), Field.builder().name("name").build())
            .withNearImage(nearImage)
            .withLimit(1)
            .run()

        if (result.hasErrors()) {
            System.err.println(result.error);
            return;
        }
        val input = result.result.data.toString()
        val image = extractValue(input, "image")
        val name = extractValue(input, "name")
        if (image != null && image != "null") {
            val decoded = Base64.getDecoder().decode(image)
            printImage(decoded)
        }
        if (name != null && name != "null") {
            println(name)
        }
        logger.info { "Weaviate successfully executed a nearby text query!" }
    }

    companion object : Log

}

fun extractValue(input: String, key: String): String? {
    val pattern = Pattern.compile("$key=([^,\\\\}]*)")
    val matcher = pattern.matcher(input)
    if (!matcher.find()) {
        return null
    }
    return matcher.group(1).trim()
}

fun printImage(fileContent: ByteArray) {
    val bufferedImage = ImageIO.read(ByteArrayInputStream(fileContent))
    val outputFile = File("output_image.jpg")
    ImageIO.write(bufferedImage, "jpg", outputFile)
    println("image saved successfully.")
}

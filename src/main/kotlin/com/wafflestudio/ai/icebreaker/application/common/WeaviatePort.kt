package com.wafflestudio.ai.icebreaker.application.common

import com.wafflestudio.ai.icebreaker.outbound.common.Question
import io.weaviate.client.v1.schema.model.Schema

interface WeaviatePort {

    fun addSchema(className: String)

    fun getSchema(): Schema

    fun save(className: String, data: List<Question>)

    fun nearTextQuery(className: String, text: String)
}

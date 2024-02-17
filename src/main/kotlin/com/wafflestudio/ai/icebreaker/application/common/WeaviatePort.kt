package com.wafflestudio.ai.icebreaker.application.common

import io.weaviate.client.v1.schema.model.Schema

interface WeaviatePort {

    fun addSchema(className: String)

    fun getSchema(): Schema

    fun save(className: String, id: Int, description: String)

    fun getAll(className: String)

    fun nearTextQuery(className: String, keywords: List<String>): Int?
}

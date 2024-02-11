package com.wafflestudio.ai.icebreaker.application.common

import io.weaviate.client.v1.schema.model.Schema

interface WeaviatePort {

    fun addSchema(className: String)

    fun getSchema(): Schema

    fun save(className: String, text: String)

    fun saveImage(className: String, fileContent: ByteArray)

    fun getAll(className: String)

    fun searchNearText(className: String, text: String)

    fun searchNearImage(className: String, fileContent: ByteArray)

}

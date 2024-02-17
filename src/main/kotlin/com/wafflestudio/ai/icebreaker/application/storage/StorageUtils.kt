package com.wafflestudio.ai.icebreaker.application.storage

import org.springframework.http.MediaType
import java.nio.file.Path
import java.nio.file.Paths

fun getFileExtension(fileName: String): MediaType {
    val ext = fileName.substringAfterLast(".", "")
    return when (ext) {
        "jpg", "jpeg" -> MediaType.IMAGE_JPEG
        "png" -> MediaType.IMAGE_PNG
        else -> throw RuntimeException("$ext 이미지 확장자 지원 안함")
    }
}

fun getFileExtensionValue(fileName: String): String {
    return when (val ext = getFileExtension(fileName)) {
        MediaType.IMAGE_JPEG -> MediaType.IMAGE_JPEG_VALUE
        MediaType.IMAGE_PNG -> MediaType.IMAGE_PNG_VALUE
        else -> throw RuntimeException("$ext 이미지 확장자 지원 안함")
    }
}

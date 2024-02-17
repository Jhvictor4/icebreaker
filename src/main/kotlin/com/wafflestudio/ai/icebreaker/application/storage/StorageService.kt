package com.wafflestudio.ai.icebreaker.application.storage

import com.wafflestudio.ai.icebreaker.application.Log
import com.wafflestudio.ai.icebreaker.application.storage.port.StorageUseCase
import jakarta.annotation.PostConstruct
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.IOException
import java.nio.file.Files
import java.util.*

@Service
class StorageService : StorageUseCase {

    @PostConstruct
    fun start() {
        try {
            if (!Files.exists(root)) {
                Files.createDirectory(root)
            }
        } catch (e: IOException) {
            throw RuntimeException()
        }
    }

    override fun save(userId: Long, filePartFlux: Flux<FilePart>): Mono<List<String>> {
        return filePartFlux.doOnNext { logger.info { "upload image" } }.flatMap { filePart ->
            val userDirectory = root.resolve(userId.toString())
            getFileExtension(filePart.filename())
            if (!Files.exists(userDirectory)) {
                Files.createDirectories(userDirectory)
            }

            val uuid = UUID.randomUUID()
            val filename = "${uuid}_${filePart.filename()}"
            filePart.transferTo(userDirectory.resolve(filename))
                .then(Mono.just(filename))
        }.collectList()
    }

    private fun getFileExtension(fileName: String): MediaType {
        val ext = fileName.substringAfterLast(".", "")
        return when (ext) {
            "jpg", "jpeg" -> MediaType.IMAGE_JPEG
            "png" -> MediaType.IMAGE_PNG
            else -> throw RuntimeException("$ext 이미지 확장자 지원 안함")
        }
    }

    companion object : Log

}
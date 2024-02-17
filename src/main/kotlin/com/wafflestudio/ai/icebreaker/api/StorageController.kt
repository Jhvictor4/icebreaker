package com.wafflestudio.ai.icebreaker.api

import com.google.common.net.HttpHeaders
import com.wafflestudio.ai.icebreaker.application.preprocessing.PreprocessingUseCase
import com.wafflestudio.ai.icebreaker.application.storage.port.StorageUseCase
import io.grpc.Server
import kotlinx.coroutines.reactive.asFlow
import org.springframework.core.io.UrlResource
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.file.Paths


@RestController
@RequestMapping("/api/v1")
class StorageController(
    val storageUseCase: StorageUseCase,
    val preprocessingUserCase: PreprocessingUseCase,
) {

    @PostMapping("/upload")
    suspend fun uploadImages(
        @RequestPart("files") filePartFlux: Flux<FilePart>
    ): Mono<List<String>> {
        return storageUseCase.save(1, filePartFlux).flatMap {
            preprocessingUserCase.summarizeImages(it)
            Mono.just(it)
        }
    }

    @GetMapping("/image/{filename}")
    fun getImage(@PathVariable filename: String): ResponseEntity<UrlResource> {
        val filePath = Paths.get("/tmp/1/${filename}")
        val resource = UrlResource(filePath.toUri())
        if (!resource.exists() && !resource.isReadable) {
            throw RuntimeException()
        }
        return ResponseEntity.ok()
            .contentType(getFileExtension(filename))
            .body(resource)
    }

    private fun getFileExtension(fileName: String): MediaType {
        val ext = fileName.substringAfterLast(".", "")
        return when (ext) {
            "jpg", "jpeg" -> MediaType.IMAGE_JPEG
            "png" -> MediaType.IMAGE_PNG
            else -> throw RuntimeException("$ext 이미지 확장자 지원 안함")
        }
    }

}
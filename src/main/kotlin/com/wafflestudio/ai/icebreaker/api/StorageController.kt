package com.wafflestudio.ai.icebreaker.api

import com.wafflestudio.ai.icebreaker.application.preprocessing.PreprocessingUseCase
import com.wafflestudio.ai.icebreaker.application.storage.getFileExtension
import com.wafflestudio.ai.icebreaker.application.storage.port.StorageUseCase
import com.wafflestudio.ai.icebreaker.application.user.User
import org.springframework.core.io.UrlResource
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.file.Paths

@RestController
@RequestMapping("/api/v1")
class StorageController(
    val storageUseCase: StorageUseCase,
    val preprocessingUserCase: PreprocessingUseCase
) {

    @PostMapping("/upload")
    fun uploadImages(
        user: User,
        @RequestPart("files") filePartFlux: Flux<FilePart>
    ): Mono<List<String>> {
        return storageUseCase.save(user.id, filePartFlux).flatMap {
            preprocessingUserCase.summarizeImages(user.id, it)
            Mono.just(it)
        }
    }

    @GetMapping("/image/{filename}")
    fun getImage(user: User, @PathVariable filename: String): ResponseEntity<UrlResource> {
        val filePath = Paths.get("${storageUseCase.getRoot()}/${user.id}/$filename")
        val resource = UrlResource(filePath.toUri())
        if (!resource.exists() && !resource.isReadable) {
            throw RuntimeException()
        }
        return ResponseEntity.ok()
            .contentType(getFileExtension(filename))
            .body(resource)
    }
}

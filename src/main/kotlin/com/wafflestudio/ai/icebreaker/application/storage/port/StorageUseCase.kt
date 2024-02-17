package com.wafflestudio.ai.icebreaker.application.storage.port

import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.file.Path
import java.nio.file.Paths

interface StorageUseCase {

    val root: Path
        get() = Paths.get("/tmp")

    fun save(userId: Long, filePartFlux: Flux<FilePart>): Mono<List<String>>

}
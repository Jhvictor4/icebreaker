package com.wafflestudio.ai.icebreaker.application.storage.port

import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface StorageUseCase {

    fun save(userId: Long, filePartFlux: Flux<FilePart>): Mono<List<String>>
}

package com.wafflestudio.ai.icebreaker.application.storage

import com.wafflestudio.ai.icebreaker.application.Log
import com.wafflestudio.ai.icebreaker.application.storage.port.StorageUseCase
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Service
class StorageService(
    @Value("\${storage.root}") rootPath: String
) : StorageUseCase {
    val rootPath: Path = Paths.get(rootPath)

    @PostConstruct
    fun start() {
        try {
            if (!Files.exists(rootPath)) {
                Files.createDirectory(rootPath)
            }
        } catch (e: IOException) {
            throw RuntimeException()
        }
    }

    override fun getRoot(): String {
        return rootPath.toString()
    }

    override fun save(userId: Long, filePartFlux: Flux<FilePart>): Mono<List<String>> {
        return filePartFlux.flatMap { filePart ->
            val userDirectory = rootPath.resolve(userId.toString())
            getFileExtension(filePart.filename())
            if (!Files.exists(userDirectory)) {
                Files.createDirectories(userDirectory)
            }

            val uuid = UUID.randomUUID()
            val filename = "${uuid}_${filePart.filename()}"
            val path = userDirectory.resolve(filename)
            filePart.transferTo(path)
                .then(Mono.just(filename))
        }.collectList()
    }

    companion object : Log
}

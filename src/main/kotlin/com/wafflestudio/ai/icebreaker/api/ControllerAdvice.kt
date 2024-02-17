package com.wafflestudio.ai.icebreaker.api

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

sealed class ApplicationException(
    override val message: String
) : RuntimeException(message) {

    data class Common(override val message: String) : ApplicationException(message)
}

@RestControllerAdvice
class ControllerAdvice {

    @ExceptionHandler(ApplicationException::class)
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun handleException(e: Exception): ErrorResponse {
        return ErrorResponse(e.message ?: "Unknown error")
    }

    data class ErrorResponse(
        val error: String
    )
}

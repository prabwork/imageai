package com.prabwork.ai.imageai

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ImageExceptionHandler {

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): ResponseEntity<ErrorResponse?> {
        val error = ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.message,
            System.currentTimeMillis()
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ErrorResponse?> {
        val error = ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.message,
            System.currentTimeMillis()
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @JvmRecord
    data class ErrorResponse(val status: Int, val message: String?, val timestamp: Long)
}
package com.midasit.mcafe.infra.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class CustomExceptionHandler {

    @ExceptionHandler(CustomException::class)
    fun handle(e: CustomException): ResponseEntity<String> {
        val errorMessage = e.errorMessage
        return ResponseEntity(errorMessage.message, errorMessage.httpStatus)
    }
}
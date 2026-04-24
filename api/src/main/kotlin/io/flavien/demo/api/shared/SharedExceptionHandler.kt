package io.flavien.demo.api.shared

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class SharedExceptionHandler {
    @ExceptionHandler(FioException::class)
    fun handleFioException(ex: FioException): ProblemDetail {
        val status =
            ex::class.java
                .getAnnotation(ResponseStatus::class.java)
                ?.value
                ?: HttpStatus.INTERNAL_SERVER_ERROR

        val problemDetail = ProblemDetail.forStatus(status)
        problemDetail.title = status.reasonPhrase
        problemDetail.detail = ex.message
        return problemDetail
    }
}

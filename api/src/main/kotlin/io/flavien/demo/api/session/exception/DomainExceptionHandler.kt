package io.flavien.demo.api.session.exception

import io.flavien.demo.domain.session.exception.InvalidOtpException
import io.flavien.demo.domain.session.exception.OtpRequiredException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class DomainExceptionHandler {
    @ExceptionHandler(OtpRequiredException::class)
    fun handleOtpRequiredException(ex: OtpRequiredException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED)
        problemDetail.title = "Unauthorized"
        problemDetail.detail = ex.message
        return problemDetail
    }

    @ExceptionHandler(InvalidOtpException::class)
    fun handleInvalidOtpException(ex: InvalidOtpException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED)
        problemDetail.title = "Unauthorized"
        problemDetail.detail = ex.message
        return problemDetail
    }
}

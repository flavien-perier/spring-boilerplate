package io.flavien.demo.domain.shared.exception

import org.springframework.http.HttpStatus

abstract class FioException(
    message: String,
    val httpStatus: HttpStatus,
    val errorCode: String,
) : RuntimeException(message)

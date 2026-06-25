package io.flavien.demo.domain.user.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class UserNotFoundException(
    message: String,
) : FioException(message, HttpStatus.NOT_FOUND, "USER_NOT_FOUND")

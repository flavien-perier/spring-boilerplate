package io.flavien.demo.domain.user.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class UserAlreadyExistsException(
    email: String,
) : FioException("User $email already exists", HttpStatus.CONFLICT, "USER_ALREADY_EXISTS")

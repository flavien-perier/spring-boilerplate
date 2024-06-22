package io.flavien.demo.user.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "User already exists")
class UserAlreadyExistsException(email: String) : RuntimeException("User $email already exists")
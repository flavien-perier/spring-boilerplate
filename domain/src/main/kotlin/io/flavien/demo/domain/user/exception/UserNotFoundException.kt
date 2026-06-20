package io.flavien.demo.domain.user.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class UserNotFoundException : FioException {
    constructor(email: String) : super("User ($email) not found", HttpStatus.NOT_FOUND, "USER_NOT_FOUND")
    constructor(userId: Long) : super("User (id: $userId) not found", HttpStatus.NOT_FOUND, "USER_NOT_FOUND")
}

package io.flavien.demo.user.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "User not found")
class UserNotFoundException : RuntimeException {
    constructor(email: String) : super("User ($email) not found")
    constructor(userId: Long) : super("User (id: $userId) not found")
}
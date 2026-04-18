package io.flavien.demo.domain.user.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class UserNotFoundException : RuntimeException {
    constructor(email: String) : super("User ($email) not found")
    constructor(userId: Long) : super("User (id: $userId) not found")
}

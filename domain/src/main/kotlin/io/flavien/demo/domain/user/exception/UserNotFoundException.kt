package io.flavien.demo.domain.user.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class UserNotFoundException : FioException {
    constructor(email: String) : super("User ($email) not found")
    constructor(userId: Long) : super("User (id: $userId) not found")
}

package io.flavien.demo.domain.session.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
class UserIsDisabledException(
    email: String,
) : RuntimeException("user $email is disabled")

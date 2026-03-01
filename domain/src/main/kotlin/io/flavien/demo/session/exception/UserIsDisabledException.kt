package io.flavien.demo.session.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "User is disabled")
class UserIsDisabledException(email: String) : RuntimeException("user $email is disabled")
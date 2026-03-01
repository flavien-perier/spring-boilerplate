package io.flavien.demo.session.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Authentication failed")
class AuthenticationFailedException : RuntimeException("Authentication failed")
package io.flavien.demo.session.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Password format does not respect all constraints")
class BadPasswordFormatException : RuntimeException("Password format does not respect all constraints")
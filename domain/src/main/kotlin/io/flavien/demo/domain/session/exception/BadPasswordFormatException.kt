package io.flavien.demo.domain.session.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
class BadPasswordFormatException : RuntimeException("Password format does not respect all constraints")

package io.flavien.demo.session.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Impossible to create an refresh token")
class BadRefreshTokenCreationException : RuntimeException("Impossible to create an refresh token")
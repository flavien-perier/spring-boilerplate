package io.flavien.demo.session.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Impossible to create an access token")
class BadAccessTokenCreationException : RuntimeException("Impossible to create an access token")
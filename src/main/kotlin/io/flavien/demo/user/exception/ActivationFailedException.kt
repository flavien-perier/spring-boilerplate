package io.flavien.demo.user.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Activation failed")
class ActivationFailedException : RuntimeException("Activation failed")
package io.flavien.demo.user.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Change password failed")
class ChangePasswordFailedException : RuntimeException("Change password failed")
package io.flavien.demo.domain.session.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class UserIsDisabledException(
    email: String,
) : FioException("user $email is disabled", HttpStatus.UNAUTHORIZED, "USER_DISABLED")

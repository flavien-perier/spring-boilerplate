package io.flavien.demo.domain.user.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class ChangePasswordFailedException :
    FioException("Change password failed", HttpStatus.UNAUTHORIZED, "CHANGE_PASSWORD_FAILED")

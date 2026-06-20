package io.flavien.demo.domain.user.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class ActivationFailedException :
    FioException("Activation failed", HttpStatus.UNAUTHORIZED, "ACTIVATION_FAILED")

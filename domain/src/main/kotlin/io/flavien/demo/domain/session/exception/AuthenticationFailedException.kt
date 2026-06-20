package io.flavien.demo.domain.session.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class AuthenticationFailedException :
    FioException("Authentication failed", HttpStatus.UNAUTHORIZED, "AUTHENTICATION_FAILED")

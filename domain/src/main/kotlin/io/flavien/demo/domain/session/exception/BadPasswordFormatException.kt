package io.flavien.demo.domain.session.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class BadPasswordFormatException :
    FioException("Password format does not respect all constraints", HttpStatus.UNAUTHORIZED, "BAD_PASSWORD_FORMAT")

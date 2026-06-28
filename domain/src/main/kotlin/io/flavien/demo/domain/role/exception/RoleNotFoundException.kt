package io.flavien.demo.domain.role.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class RoleNotFoundException(
    message: String,
) : FioException(message, HttpStatus.NOT_FOUND, "ROLE_NOT_FOUND")

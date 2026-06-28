package io.flavien.demo.domain.role.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class RoleAlreadyExistsException(
    name: String,
) : FioException("Role $name already exists", HttpStatus.CONFLICT, "ROLE_ALREADY_EXISTS")

package io.flavien.demo.domain.role.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class ProtectedRoleException(
    name: String,
) : FioException("Role $name is protected and cannot be deleted", HttpStatus.CONFLICT, "PROTECTED_ROLE")

package io.flavien.demo.domain.user.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class UserRoleNotFoundException(
    role: String,
) : FioException("User role ($role) not found", HttpStatus.NOT_FOUND, "USER_ROLE_NOT_FOUND")

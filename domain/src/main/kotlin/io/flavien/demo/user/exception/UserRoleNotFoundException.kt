package io.flavien.demo.user.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "User Role Not Found")
class UserRoleNotFoundException(role: String) : RuntimeException("User role ($role) not found")
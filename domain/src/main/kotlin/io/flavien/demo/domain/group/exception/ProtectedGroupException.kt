package io.flavien.demo.domain.group.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class ProtectedGroupException(
    name: String,
) : FioException("Group $name is protected and cannot be deleted", HttpStatus.CONFLICT, "PROTECTED_GROUP")

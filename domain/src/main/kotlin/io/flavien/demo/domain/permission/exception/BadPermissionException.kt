package io.flavien.demo.domain.permission.exception

import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class BadPermissionException(
    permission: PermissionEnum,
) : FioException(
        "Missing permission: ${permission.name}",
        HttpStatus.FORBIDDEN,
        "BAD_PERMISSION",
    )

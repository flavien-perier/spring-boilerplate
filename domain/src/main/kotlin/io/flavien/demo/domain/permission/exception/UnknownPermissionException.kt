package io.flavien.demo.domain.permission.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class UnknownPermissionException(
    value: String,
) : FioException("Unknown permission: $value", HttpStatus.BAD_REQUEST, "UNKNOWN_PERMISSION")

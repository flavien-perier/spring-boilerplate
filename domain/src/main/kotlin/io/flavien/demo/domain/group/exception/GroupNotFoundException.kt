package io.flavien.demo.domain.group.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class GroupNotFoundException(
    message: String,
) : FioException(message, HttpStatus.NOT_FOUND, "GROUP_NOT_FOUND")

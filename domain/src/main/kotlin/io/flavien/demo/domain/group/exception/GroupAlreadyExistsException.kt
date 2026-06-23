package io.flavien.demo.domain.group.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class GroupAlreadyExistsException(
    name: String,
) : FioException("Group $name already exists", HttpStatus.CONFLICT, "GROUP_ALREADY_EXISTS")

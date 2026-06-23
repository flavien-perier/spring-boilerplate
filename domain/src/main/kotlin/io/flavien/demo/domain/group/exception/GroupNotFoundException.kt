package io.flavien.demo.domain.group.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class GroupNotFoundException : FioException {
    constructor(groupId: Long) : super("Group (id: $groupId) not found", HttpStatus.NOT_FOUND, "GROUP_NOT_FOUND")
    constructor(name: String) : super("Group ($name) not found", HttpStatus.NOT_FOUND, "GROUP_NOT_FOUND")
}

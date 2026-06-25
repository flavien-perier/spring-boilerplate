package io.flavien.demo.domain.group.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus
import java.util.UUID

class GroupHierarchyException : FioException {
    constructor(groupId: UUID, parentId: UUID) : super(
        "Group (id: $parentId) cannot be set as parent of group (id: $groupId): this would create a cycle",
        HttpStatus.BAD_REQUEST,
        "GROUP_HIERARCHY",
    )

    constructor(groupId: UUID) : super(
        "Group (id: $groupId) has child groups and cannot be deleted",
        HttpStatus.CONFLICT,
        "GROUP_HAS_CHILDREN",
    )
}

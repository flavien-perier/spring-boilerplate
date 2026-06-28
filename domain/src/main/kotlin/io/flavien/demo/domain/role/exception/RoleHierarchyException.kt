package io.flavien.demo.domain.role.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus
import java.util.UUID

class RoleHierarchyException : FioException {
    constructor(roleId: UUID, parentId: UUID) : super(
        "Role (id: $parentId) cannot be set as parent of role (id: $roleId): this would create a cycle",
        HttpStatus.BAD_REQUEST,
        "ROLE_HIERARCHY",
    )

    constructor(roleId: UUID) : super(
        "Role (id: $roleId) has child roles and cannot be deleted",
        HttpStatus.CONFLICT,
        "ROLE_HAS_CHILDREN",
    )
}

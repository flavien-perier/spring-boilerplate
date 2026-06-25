package io.flavien.demo.domain.permission.model.id

import io.flavien.demo.domain.permission.model.PermissionEnum
import java.io.Serializable
import java.util.UUID

data class GroupPermissionId(
    val group: UUID = UUID(0L, 0L),
    val permission: PermissionEnum? = null,
) : Serializable

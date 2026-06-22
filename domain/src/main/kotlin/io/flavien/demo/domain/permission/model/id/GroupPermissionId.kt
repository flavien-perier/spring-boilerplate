package io.flavien.demo.domain.permission.model.id

import io.flavien.demo.domain.permission.model.PermissionEnum
import java.io.Serializable

data class GroupPermissionId(
    val group: Long = 0,
    val permission: PermissionEnum? = null,
) : Serializable

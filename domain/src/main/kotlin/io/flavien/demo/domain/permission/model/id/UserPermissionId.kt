package io.flavien.demo.domain.permission.model.id

import io.flavien.demo.domain.permission.model.PermissionEnum
import java.io.Serializable

data class UserPermissionId(
    val user: Long = 0,
    val permission: PermissionEnum? = null,
) : Serializable

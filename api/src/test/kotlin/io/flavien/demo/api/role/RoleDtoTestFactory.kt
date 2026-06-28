package io.flavien.demo.api.role

import io.flavien.demo.api.generated.dto.PermissionSettingDto
import io.flavien.demo.api.generated.dto.PermissionUpdateDto
import io.flavien.demo.api.generated.dto.RoleCreationDto
import io.flavien.demo.api.generated.dto.RoleDto
import io.flavien.demo.api.generated.dto.RoleUpdateDto

object RoleDtoTestFactory {
    fun initRoleDto(
        id: String = "role-1",
        name: String = "ROLE",
        parentId: String? = null,
        parentName: String? = null,
    ) = RoleDto(id, name, parentId, parentName)

    fun initRoleCreationDto(
        name: String = "ROLE",
        parentId: String? = null,
    ) = RoleCreationDto(name, parentId)

    fun initRoleUpdateDto(
        name: String? = "ROLE",
        parentId: String? = null,
    ) = RoleUpdateDto(name, parentId)

    fun initPermissionSettingDto(
        permission: String = "MANAGE_ALL_USERS",
        allow: Boolean? = null,
        locked: Boolean = false,
        inheritedAllow: Boolean? = null,
    ) = PermissionSettingDto(
        permission = permission,
        locked = locked,
        allow = allow,
        inheritedAllow = inheritedAllow,
    )

    fun initPermissionUpdateDto(allow: Boolean = true) = PermissionUpdateDto(allow)
}

package io.flavien.demo.api.group

import io.flavien.demo.api.generated.dto.GroupCreationDto
import io.flavien.demo.api.generated.dto.GroupDto
import io.flavien.demo.api.generated.dto.GroupUpdateDto
import io.flavien.demo.api.generated.dto.PermissionSettingDto
import io.flavien.demo.api.generated.dto.PermissionUpdateDto

object GroupDtoTestFactory {
    fun initGroupDto(
        id: Long = 1L,
        name: String = "GROUP",
        parentId: Long? = null,
        parentName: String? = null,
    ) = GroupDto(id, name, parentId, parentName)

    fun initGroupCreationDto(
        name: String = "GROUP",
        parentId: Long? = null,
    ) = GroupCreationDto(name, parentId)

    fun initGroupUpdateDto(
        name: String? = "GROUP",
        parentId: Long? = null,
    ) = GroupUpdateDto(name, parentId)

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

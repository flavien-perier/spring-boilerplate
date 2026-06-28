package io.flavien.demo.domain.permission

import io.flavien.demo.domain.permission.entity.Permission
import io.flavien.demo.domain.permission.entity.RolePermission
import io.flavien.demo.domain.permission.entity.UserPermission
import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.role.RoleTestFactory
import io.flavien.demo.domain.role.entity.Role
import io.flavien.demo.domain.user.UserTestFactory
import io.flavien.demo.domain.user.entity.User
import java.util.UUID

object PermissionTestFactory {
    fun initUserPermission(
        user: User = UserTestFactory.initUser(id = UUID.fromString("00000000-0000-0000-0000-00000000000a")),
        permission: PermissionEnum = PermissionEnum.MANAGE_OWN_ACCOUNT,
        allow: Boolean = true,
    ) = UserPermission(user, permission, allow)

    fun initRolePermission(
        role: Role = RoleTestFactory.initRole(),
        permission: PermissionEnum = PermissionEnum.MANAGE_OWN_ACCOUNT,
        allow: Boolean = true,
    ) = RolePermission(role, permission, allow)

    fun initPermission(permission: PermissionEnum = PermissionEnum.MANAGE_OWN_ACCOUNT) = Permission(permission)
}

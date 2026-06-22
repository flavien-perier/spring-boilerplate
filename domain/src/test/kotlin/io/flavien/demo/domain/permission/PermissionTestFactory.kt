package io.flavien.demo.domain.permission

import io.flavien.demo.domain.group.GroupTestFactory
import io.flavien.demo.domain.group.entity.Group
import io.flavien.demo.domain.permission.entity.GroupPermission
import io.flavien.demo.domain.permission.entity.Permission
import io.flavien.demo.domain.permission.entity.UserPermission
import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.user.UserTestFactory
import io.flavien.demo.domain.user.entity.User

object PermissionTestFactory {
    fun initUserPermission(
        user: User = UserTestFactory.initUser(id = 1L),
        permission: PermissionEnum = PermissionEnum.MANAGE_OWN_ACCOUNT,
        allow: Boolean = true,
    ) = UserPermission(user, permission, allow)

    fun initGroupPermission(
        group: Group = GroupTestFactory.initGroup(),
        permission: PermissionEnum = PermissionEnum.MANAGE_OWN_ACCOUNT,
        allow: Boolean = true,
    ) = GroupPermission(group, permission, allow)

    fun initPermission(permission: PermissionEnum = PermissionEnum.MANAGE_OWN_ACCOUNT) = Permission(permission)
}

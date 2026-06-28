package io.flavien.demo.domain.role

import io.flavien.demo.domain.role.entity.Role
import io.flavien.demo.domain.role.entity.UserRole
import io.flavien.demo.domain.user.UserTestFactory
import io.flavien.demo.domain.user.entity.User
import java.util.UUID

object RoleTestFactory {
    fun initRole(
        id: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        name: String = "USER",
        parent: Role? = null,
    ) = Role(id = id, name = name, parent = parent)

    fun initUserRole(
        user: User = UserTestFactory.initUser(id = UUID.fromString("00000000-0000-0000-0000-00000000000a")),
        role: Role = initRole(),
    ) = UserRole(user, role)
}

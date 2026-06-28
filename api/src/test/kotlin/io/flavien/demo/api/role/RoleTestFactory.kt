package io.flavien.demo.api.role

import io.flavien.demo.domain.role.entity.Role
import java.util.UUID

object RoleTestFactory {
    fun initRole(
        id: UUID = UUID.fromString("00000000-0000-7000-8000-000000000001"),
        name: String = "ROLE",
        parent: Role? = null,
    ) = Role(id = id, name = name, parent = parent)
}

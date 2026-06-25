package io.flavien.demo.domain.group

import io.flavien.demo.domain.group.entity.Group
import io.flavien.demo.domain.group.entity.UserGroup
import io.flavien.demo.domain.user.UserTestFactory
import io.flavien.demo.domain.user.entity.User
import java.util.UUID

object GroupTestFactory {
    fun initGroup(
        id: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        name: String = "USER",
        parent: Group? = null,
    ) = Group(id = id, name = name, parent = parent)

    fun initUserGroup(
        user: User = UserTestFactory.initUser(id = UUID.fromString("00000000-0000-0000-0000-00000000000a")),
        group: Group = initGroup(),
    ) = UserGroup(user, group)
}

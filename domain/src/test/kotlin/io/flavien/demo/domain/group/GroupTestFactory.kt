package io.flavien.demo.domain.group

import io.flavien.demo.domain.group.entity.Group
import io.flavien.demo.domain.group.entity.UserGroup
import io.flavien.demo.domain.user.UserTestFactory
import io.flavien.demo.domain.user.entity.User

object GroupTestFactory {
    fun initGroup(
        id: Long = 1L,
        name: String = "USER",
        parent: Group? = null,
    ) = Group(id = id, name = name, parent = parent)

    fun initUserGroup(
        user: User = UserTestFactory.initUser(id = 1L),
        group: Group = initGroup(),
    ) = UserGroup(user, group)
}

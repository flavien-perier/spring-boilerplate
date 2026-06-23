package io.flavien.demo.api.group

import io.flavien.demo.domain.group.entity.Group

object GroupTestFactory {
    fun initGroup(
        id: Long = 1L,
        name: String = "GROUP",
        parent: Group? = null,
    ) = Group(id = id, name = name, parent = parent)
}

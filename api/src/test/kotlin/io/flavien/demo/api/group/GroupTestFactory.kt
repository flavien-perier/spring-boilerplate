package io.flavien.demo.api.group

import io.flavien.demo.domain.group.entity.Group
import java.util.UUID

object GroupTestFactory {
    fun initGroup(
        id: UUID = UUID.fromString("00000000-0000-7000-8000-000000000001"),
        name: String = "GROUP",
        parent: Group? = null,
    ) = Group(id = id, name = name, parent = parent)
}

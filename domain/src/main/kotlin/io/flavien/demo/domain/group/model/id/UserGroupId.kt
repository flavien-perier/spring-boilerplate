package io.flavien.demo.domain.group.model.id

import java.io.Serializable
import java.util.UUID

data class UserGroupId(
    val user: UUID = UUID(0L, 0L),
    val group: UUID = UUID(0L, 0L),
) : Serializable

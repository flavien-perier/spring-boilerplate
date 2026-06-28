package io.flavien.demo.domain.role.model.id

import java.io.Serializable
import java.util.UUID

data class UserRoleId(
    val user: UUID = UUID(0L, 0L),
    val role: UUID = UUID(0L, 0L),
) : Serializable

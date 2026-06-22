package io.flavien.demo.domain.group.model.id

import java.io.Serializable

data class UserGroupId(
    val user: Long = 0,
    val group: Long = 0,
) : Serializable

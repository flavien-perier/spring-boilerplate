package io.flavien.demo.domain.permission.model

import io.flavien.demo.domain.permission.exception.UnknownPermissionException

enum class PermissionEnum {
    MANAGE_OWN_ACCOUNT,
    MANAGE_OWN_SESSIONS,
    MANAGE_ALL_USERS,
    MANAGE_ALL_ROLES,
    ;

    companion object {
        private val BY_NAME: Map<String, PermissionEnum> = entries.associateBy { it.name }

        fun fromName(value: String): PermissionEnum = BY_NAME[value] ?: throw UnknownPermissionException(value)
    }
}

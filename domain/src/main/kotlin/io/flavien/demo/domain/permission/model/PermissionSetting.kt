package io.flavien.demo.domain.permission.model

data class PermissionSetting(
    val permission: PermissionEnum,
    val allow: Boolean?,
    val locked: Boolean = false,
    val inheritedAllow: Boolean? = null,
)

package io.flavien.demo.domain.permission.entity

import io.flavien.demo.domain.permission.model.PermissionEnum
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id

@Entity(name = "permission")
data class Permission(
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, updatable = false, length = 50)
    var name: PermissionEnum,
)

package io.flavien.demo.domain.permission.entity

import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.permission.model.id.RolePermissionId
import io.flavien.demo.domain.role.entity.Role
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity(name = "role_permission")
@IdClass(RolePermissionId::class)
data class RolePermission(
    // EAGER: GraalVM native images cannot create Hibernate lazy proxies at runtime.
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    var role: Role,
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "permission_name", nullable = false, length = 50)
    var permission: PermissionEnum,
    @Column(name = "allow", nullable = false)
    var allow: Boolean = true,
)

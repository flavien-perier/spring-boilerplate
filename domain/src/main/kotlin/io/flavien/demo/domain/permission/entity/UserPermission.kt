package io.flavien.demo.domain.permission.entity

import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.permission.model.id.UserPermissionId
import io.flavien.demo.domain.user.entity.User
import jakarta.persistence.*

@Entity(name = "user_permission")
@IdClass(UserPermissionId::class)
data class UserPermission(
    // EAGER: GraalVM native images cannot create Hibernate lazy proxies at runtime.
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "permission_name", nullable = false, length = 50)
    var permission: PermissionEnum,
    @Column(name = "allow", nullable = false)
    var allow: Boolean = true,
)

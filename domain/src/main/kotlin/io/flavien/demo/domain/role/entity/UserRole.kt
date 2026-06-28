package io.flavien.demo.domain.role.entity

import io.flavien.demo.domain.role.model.id.UserRoleId
import io.flavien.demo.domain.user.entity.User
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

// EAGER associations: GraalVM native images run with Hibernate BytecodeProvider 'none' and cannot
// create lazy proxies at runtime.
@Entity(name = "user_role")
@IdClass(UserRoleId::class)
data class UserRole(
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    var role: Role,
)

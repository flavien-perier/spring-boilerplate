package io.flavien.demo.domain.group.entity

import io.flavien.demo.domain.group.model.id.UserGroupId
import io.flavien.demo.domain.user.entity.User
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

// EAGER associations: GraalVM native images run with Hibernate BytecodeProvider 'none' and cannot
// create lazy proxies at runtime.
@Entity(name = "user_group")
@IdClass(UserGroupId::class)
data class UserGroup(
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false)
    var group: Group,
)

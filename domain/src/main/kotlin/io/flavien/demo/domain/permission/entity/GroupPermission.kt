package io.flavien.demo.domain.permission.entity

import io.flavien.demo.domain.group.entity.Group
import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.permission.model.id.GroupPermissionId
import jakarta.persistence.*

@Entity(name = "group_permission")
@IdClass(GroupPermissionId::class)
data class GroupPermission(
    // EAGER: GraalVM native images cannot create Hibernate lazy proxies at runtime.
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false)
    var group: Group,
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "permission_name", nullable = false, length = 50)
    var permission: PermissionEnum,
    @Column(name = "allow", nullable = false)
    var allow: Boolean = true,
)

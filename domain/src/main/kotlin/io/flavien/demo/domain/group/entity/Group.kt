package io.flavien.demo.domain.group.entity

import jakarta.persistence.*

@Entity(name = "app_group")
data class Group(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id", nullable = false, unique = true, updatable = false)
    var id: Long? = null,
    @Column(name = "name", nullable = false, unique = true)
    var name: String,
    // EAGER: GraalVM native images run with Hibernate BytecodeProvider 'none' and cannot create
    // lazy proxies at runtime; the permission resolver walks this parent chain immediately anyway.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    var parent: Group? = null,
)

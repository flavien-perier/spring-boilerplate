package io.flavien.demo.user.entity

import io.flavien.demo.user.model.UserRole
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.OffsetDateTime


@Entity(name = "app_user")
data class User(
    @Column(name = "email", nullable = false, unique = true)
    var email: String,

    @Column(name = "password", nullable = false)
    var password: String,

    @Column(name = "proof_of_work", nullable = false)
    var proofOfWork: String,

    @Column(name = "password_salt", nullable = false)
    var passwordSalt: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    var role: UserRole,

    @Column(name = "enabled", nullable = false)
    var enabled: Boolean,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, unique = true, updatable = false)
    var id: Long? = null,

    @CreationTimestamp
    @Column(name = "creation_date", nullable = false, updatable = false)
    var creationDate: OffsetDateTime? = null,

    @UpdateTimestamp
    @Column(name = "update_date", nullable = false)
    var updateDate: OffsetDateTime? = null,
)
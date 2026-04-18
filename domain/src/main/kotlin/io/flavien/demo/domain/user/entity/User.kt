package io.flavien.demo.domain.user.entity

import io.flavien.demo.domain.user.model.UserRole
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
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
    @Column(name = "otp_secret", nullable = true)
    var otpSecret: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    var role: UserRole,
    @Column(name = "enabled", nullable = false)
    var enabled: Boolean,
    @Column(name = "last_login", nullable = false)
    var lastLogin: OffsetDateTime,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, unique = true, updatable = false)
    var id: Long? = null,
    @Column(name = "deletion_warning_sent_at")
    var deletionWarningSentAt: OffsetDateTime? = null,
    @UpdateTimestamp
    @Column(name = "update_date", nullable = false)
    var updateDate: OffsetDateTime? = null,
    @CreationTimestamp
    @Column(name = "creation_date", nullable = false, updatable = false)
    var creationDate: OffsetDateTime? = null,
)

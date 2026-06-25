package io.flavien.demo.domain.user.entity

import io.flavien.demo.domain.shared.util.UuidUtil
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.PrePersist
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.OffsetDateTime
import java.util.UUID

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
    @Column(name = "enabled", nullable = false)
    var enabled: Boolean,
    @Column(name = "last_login", nullable = false)
    var lastLogin: OffsetDateTime,
    @Id
    @Column(name = "user_id", nullable = false, unique = true, updatable = false, columnDefinition = "uuid")
    var id: UUID? = null,
    @Column(name = "deletion_warning_sent_at")
    var deletionWarningSentAt: OffsetDateTime? = null,
    @UpdateTimestamp
    @Column(name = "update_date", nullable = false)
    var updateDate: OffsetDateTime? = null,
    @CreationTimestamp
    @Column(name = "creation_date", nullable = false, updatable = false)
    var creationDate: OffsetDateTime? = null,
) {
    @PrePersist
    fun prePersist() {
        if (id == null) {
            id = UuidUtil.uuidv7()
        }
    }
}

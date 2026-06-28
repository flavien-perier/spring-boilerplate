package io.flavien.demo.domain.role.entity

import io.flavien.demo.domain.shared.util.UuidUtil
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.PrePersist
import java.util.UUID

@Entity(name = "app_role")
data class Role(
    @Id
    @Column(name = "role_id", nullable = false, unique = true, updatable = false, columnDefinition = "uuid")
    var id: UUID? = null,
    @Column(name = "name", nullable = false, unique = true)
    var name: String,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    var parent: Role? = null,
) {
    @PrePersist
    fun prePersist() {
        if (id == null) {
            id = UuidUtil.uuidv7()
        }
    }
}

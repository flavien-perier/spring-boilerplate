package io.flavien.demo.domain.role.repository

import io.flavien.demo.domain.role.entity.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface RoleRepository : JpaRepository<Role, UUID> {
    fun findByName(name: String): Optional<Role>

    fun existsByName(name: String): Boolean

    fun existsByParentId(parentId: UUID): Boolean
}

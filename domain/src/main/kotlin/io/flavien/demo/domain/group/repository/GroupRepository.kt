package io.flavien.demo.domain.group.repository

import io.flavien.demo.domain.group.entity.Group
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface GroupRepository : JpaRepository<Group, UUID> {
    fun findByName(name: String): Optional<Group>

    fun existsByName(name: String): Boolean

    fun existsByParentId(parentId: UUID): Boolean
}

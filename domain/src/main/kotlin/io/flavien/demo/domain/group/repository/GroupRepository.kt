package io.flavien.demo.domain.group.repository

import io.flavien.demo.domain.group.entity.Group
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface GroupRepository : JpaRepository<Group, Long> {
    fun findByName(name: String): Optional<Group>
}

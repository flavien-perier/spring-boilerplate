package io.flavien.demo.domain.group.repository

import io.flavien.demo.domain.group.entity.UserGroup
import io.flavien.demo.domain.group.model.id.UserGroupId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserGroupRepository : JpaRepository<UserGroup, UserGroupId> {
    fun findByUserId(userId: UUID): List<UserGroup>

    fun findByUserId(
        userId: UUID,
        pageable: Pageable,
    ): Page<UserGroup>

    @Modifying
    @Query("DELETE FROM user_group ug WHERE ug.user.id = :userId")
    fun deleteByUserId(userId: UUID)

    @Modifying
    @Query("DELETE FROM user_group ug WHERE ug.group.id = :groupId")
    fun deleteByGroupId(groupId: UUID)
}

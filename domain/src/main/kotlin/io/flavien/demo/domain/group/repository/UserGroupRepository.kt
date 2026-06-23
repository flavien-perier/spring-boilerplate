package io.flavien.demo.domain.group.repository

import io.flavien.demo.domain.group.entity.UserGroup
import io.flavien.demo.domain.group.model.id.UserGroupId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserGroupRepository : JpaRepository<UserGroup, UserGroupId> {
    fun findByUserId(userId: Long): List<UserGroup>

    @Modifying
    @Query("DELETE FROM user_group ug WHERE ug.user.id = :userId")
    fun deleteByUserId(userId: Long)

    @Modifying
    @Query("DELETE FROM user_group ug WHERE ug.group.id = :groupId")
    fun deleteByGroupId(groupId: Long)
}

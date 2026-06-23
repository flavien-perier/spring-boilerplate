package io.flavien.demo.domain.permission.repository

import io.flavien.demo.domain.permission.entity.GroupPermission
import io.flavien.demo.domain.permission.model.id.GroupPermissionId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface GroupPermissionRepository : JpaRepository<GroupPermission, GroupPermissionId> {
    fun findByGroupId(groupId: Long): List<GroupPermission>

    @Modifying
    @Query("DELETE FROM group_permission gp WHERE gp.group.id = :groupId")
    fun deleteByGroupId(groupId: Long)
}

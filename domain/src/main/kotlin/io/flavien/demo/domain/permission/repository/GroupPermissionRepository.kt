package io.flavien.demo.domain.permission.repository

import io.flavien.demo.domain.permission.entity.GroupPermission
import io.flavien.demo.domain.permission.model.id.GroupPermissionId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface GroupPermissionRepository : JpaRepository<GroupPermission, GroupPermissionId> {
    fun findByGroupId(groupId: UUID): List<GroupPermission>

    @Modifying
    @Query("DELETE FROM group_permission gp WHERE gp.group.id = :groupId")
    fun deleteByGroupId(groupId: UUID)
}

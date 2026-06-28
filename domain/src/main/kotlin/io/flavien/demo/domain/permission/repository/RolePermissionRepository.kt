package io.flavien.demo.domain.permission.repository

import io.flavien.demo.domain.permission.entity.RolePermission
import io.flavien.demo.domain.permission.model.id.RolePermissionId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RolePermissionRepository : JpaRepository<RolePermission, RolePermissionId> {
    fun findByRoleId(roleId: UUID): List<RolePermission>

    @Modifying
    @Query("DELETE FROM role_permission gp WHERE gp.role.id = :roleId")
    fun deleteByRoleId(roleId: UUID)
}

package io.flavien.demo.domain.permission.repository

import io.flavien.demo.domain.permission.entity.UserPermission
import io.flavien.demo.domain.permission.model.id.UserPermissionId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserPermissionRepository : JpaRepository<UserPermission, UserPermissionId> {
    fun findByUserId(userId: UUID): List<UserPermission>

    @Modifying
    @Query("DELETE FROM user_permission up WHERE up.user.id = :userId")
    fun deleteByUserId(userId: UUID)
}

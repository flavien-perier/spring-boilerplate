package io.flavien.demo.domain.role.repository

import io.flavien.demo.domain.role.entity.UserRole
import io.flavien.demo.domain.role.model.id.UserRoleId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRoleRepository : JpaRepository<UserRole, UserRoleId> {
    fun findByUserId(userId: UUID): List<UserRole>

    fun findByUserId(
        userId: UUID,
        pageable: Pageable,
    ): Page<UserRole>

    @Modifying
    @Query("DELETE FROM user_role ug WHERE ug.user.id = :userId")
    fun deleteByUserId(userId: UUID)

    @Modifying
    @Query("DELETE FROM user_role ug WHERE ug.role.id = :roleId")
    fun deleteByRoleId(roleId: UUID)
}

package io.flavien.demo.domain.permission.repository

import io.flavien.demo.domain.permission.entity.Permission
import io.flavien.demo.domain.permission.model.PermissionEnum
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PermissionRepository : JpaRepository<Permission, PermissionEnum>

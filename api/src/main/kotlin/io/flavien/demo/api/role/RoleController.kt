package io.flavien.demo.api.role

import io.flavien.demo.api.generated.api.RoleApi
import io.flavien.demo.api.generated.dto.PermissionSettingDto
import io.flavien.demo.api.generated.dto.PermissionUpdateDto
import io.flavien.demo.api.generated.dto.RoleCreationDto
import io.flavien.demo.api.generated.dto.RoleDto
import io.flavien.demo.api.generated.dto.RolePageDto
import io.flavien.demo.api.generated.dto.RoleUpdateDto
import io.flavien.demo.api.permission.mapper.PermissionMapper
import io.flavien.demo.api.role.mapper.RoleMapper
import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.permission.service.PermissionService
import io.flavien.demo.domain.role.service.RoleService
import io.flavien.demo.domain.shared.util.PageableUtil
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import java.util.UUID

@Controller
class RoleController(
    private val roleService: RoleService,
    private val permissionService: PermissionService,
    private val roleMapper: RoleMapper,
    private val permissionMapper: PermissionMapper,
) : RoleApi {
    override fun createRole(roleCreationDto: RoleCreationDto): ResponseEntity<RoleDto> {
        val parentId = roleCreationDto.parentId?.let { UUID.fromString(it) }
        val role = roleService.create(roleCreationDto.name, parentId)
        return ResponseEntity.ok(roleMapper.toRoleDto(role))
    }

    override fun findRoles(
        page: Int?,
        pageSize: Int?,
        sortColumn: String?,
        sortOrder: String?,
    ): ResponseEntity<RolePageDto> {
        val pageable = PageableUtil.toPageable(page, pageSize, sortColumn, sortOrder, "name")
        val roles = roleService.findAll(pageable)
        return ResponseEntity.ok(roleMapper.toRolePageDto(roles))
    }

    override fun getRole(roleId: String): ResponseEntity<RoleDto> {
        val role = roleService.getById(UUID.fromString(roleId))
        return ResponseEntity.ok(roleMapper.toRoleDto(role))
    }

    override fun updateRole(
        roleId: String,
        roleUpdateDto: RoleUpdateDto,
    ): ResponseEntity<RoleDto> {
        val id = UUID.fromString(roleId)
        val parentId = roleUpdateDto.parentId?.let { UUID.fromString(it) }
        val role = roleService.update(id, roleUpdateDto.name, parentId)
        return ResponseEntity.ok(roleMapper.toRoleDto(role))
    }

    override fun deleteRole(roleId: String): ResponseEntity<Unit> {
        roleService.delete(UUID.fromString(roleId))
        return ResponseEntity.noContent().build()
    }

    override fun getRolePermissions(roleId: String): ResponseEntity<List<PermissionSettingDto>> {
        val settings = permissionService.getRolePermissions(UUID.fromString(roleId))
        return ResponseEntity.ok(permissionMapper.toPermissionSettingDtoList(settings))
    }

    override fun setRolePermission(
        roleId: String,
        permission: String,
        permissionUpdateDto: PermissionUpdateDto,
    ): ResponseEntity<Unit> {
        val permissionEnum = PermissionEnum.fromName(permission)
        permissionService.setRolePermission(UUID.fromString(roleId), permissionEnum, permissionUpdateDto.allow)
        return ResponseEntity.noContent().build()
    }

    override fun removeRolePermission(
        roleId: String,
        permission: String,
    ): ResponseEntity<Unit> {
        val permissionEnum = PermissionEnum.fromName(permission)
        permissionService.removeRolePermission(UUID.fromString(roleId), permissionEnum)
        return ResponseEntity.noContent().build()
    }
}

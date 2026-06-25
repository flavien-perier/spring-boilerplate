package io.flavien.demo.api.group

import io.flavien.demo.api.generated.api.GroupApi
import io.flavien.demo.api.generated.dto.GroupCreationDto
import io.flavien.demo.api.generated.dto.GroupDto
import io.flavien.demo.api.generated.dto.GroupUpdateDto
import io.flavien.demo.api.generated.dto.PermissionSettingDto
import io.flavien.demo.api.generated.dto.PermissionUpdateDto
import io.flavien.demo.api.group.mapper.GroupMapper
import io.flavien.demo.domain.group.service.GroupService
import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.permission.service.PermissionService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import java.util.UUID

@Controller
class GroupController(
    private val groupService: GroupService,
    private val permissionService: PermissionService,
    private val groupMapper: GroupMapper,
) : GroupApi {
    override fun createGroup(groupCreationDto: GroupCreationDto): ResponseEntity<GroupDto> {
        val parentId = groupCreationDto.parentId?.let { UUID.fromString(it) }
        val group = groupService.create(groupCreationDto.name, parentId)
        return ResponseEntity.ok(groupMapper.toGroupDto(group))
    }

    override fun findGroups(): ResponseEntity<List<GroupDto>> {
        val groups = groupService.findAll()
        return ResponseEntity.ok(groups.map { groupMapper.toGroupDto(it) })
    }

    override fun getGroup(groupId: String): ResponseEntity<GroupDto> {
        val group = groupService.getById(UUID.fromString(groupId))
        return ResponseEntity.ok(groupMapper.toGroupDto(group))
    }

    override fun updateGroup(
        groupId: String,
        groupUpdateDto: GroupUpdateDto,
    ): ResponseEntity<GroupDto> {
        val id = UUID.fromString(groupId)
        val parentId = groupUpdateDto.parentId?.let { UUID.fromString(it) }
        val group = groupService.update(id, groupUpdateDto.name, parentId)
        return ResponseEntity.ok(groupMapper.toGroupDto(group))
    }

    override fun deleteGroup(groupId: String): ResponseEntity<Unit> {
        groupService.delete(UUID.fromString(groupId))
        return ResponseEntity.noContent().build()
    }

    override fun getGroupPermissions(groupId: String): ResponseEntity<List<PermissionSettingDto>> {
        val settings = permissionService.getGroupPermissions(UUID.fromString(groupId))
        return ResponseEntity.ok(
            settings.map {
                PermissionSettingDto(
                    permission = it.permission.name,
                    locked = it.locked,
                    allow = it.allow,
                    inheritedAllow = it.inheritedAllow,
                )
            },
        )
    }

    override fun setGroupPermission(
        groupId: String,
        permission: String,
        permissionUpdateDto: PermissionUpdateDto,
    ): ResponseEntity<Unit> {
        val permissionEnum = PermissionEnum.fromName(permission)
        permissionService.setGroupPermission(UUID.fromString(groupId), permissionEnum, permissionUpdateDto.allow)
        return ResponseEntity.noContent().build()
    }

    override fun removeGroupPermission(
        groupId: String,
        permission: String,
    ): ResponseEntity<Unit> {
        val permissionEnum = PermissionEnum.fromName(permission)
        permissionService.removeGroupPermission(UUID.fromString(groupId), permissionEnum)
        return ResponseEntity.noContent().build()
    }
}

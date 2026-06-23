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

@Controller
class GroupController(
    private val groupService: GroupService,
    private val permissionService: PermissionService,
    private val groupMapper: GroupMapper,
) : GroupApi {
    override fun createGroup(groupCreationDto: GroupCreationDto): ResponseEntity<GroupDto> {
        val group = groupService.create(groupCreationDto.name, groupCreationDto.parentId)
        return ResponseEntity.ok(groupMapper.toGroupDto(group))
    }

    override fun findGroups(): ResponseEntity<List<GroupDto>> {
        val groups = groupService.findAll()
        return ResponseEntity.ok(groups.map { groupMapper.toGroupDto(it) })
    }

    override fun getGroup(groupId: Long): ResponseEntity<GroupDto> {
        val group = groupService.get(groupId)
        return ResponseEntity.ok(groupMapper.toGroupDto(group))
    }

    override fun updateGroup(
        groupId: Long,
        groupUpdateDto: GroupUpdateDto,
    ): ResponseEntity<GroupDto> {
        val group = groupService.update(groupId, groupUpdateDto.name, groupUpdateDto.parentId)
        return ResponseEntity.ok(groupMapper.toGroupDto(group))
    }

    override fun deleteGroup(groupId: Long): ResponseEntity<Unit> {
        groupService.delete(groupId)
        return ResponseEntity.noContent().build()
    }

    override fun getGroupPermissions(groupId: Long): ResponseEntity<List<PermissionSettingDto>> {
        val settings = permissionService.getGroupPermissions(groupId)
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
        groupId: Long,
        permission: String,
        permissionUpdateDto: PermissionUpdateDto,
    ): ResponseEntity<Unit> {
        val permissionEnum = PermissionEnum.fromName(permission)
        permissionService.setGroupPermission(groupId, permissionEnum, permissionUpdateDto.allow)
        return ResponseEntity.noContent().build()
    }

    override fun removeGroupPermission(
        groupId: Long,
        permission: String,
    ): ResponseEntity<Unit> {
        val permissionEnum = PermissionEnum.fromName(permission)
        permissionService.removeGroupPermission(groupId, permissionEnum)
        return ResponseEntity.noContent().build()
    }
}

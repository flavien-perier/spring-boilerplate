package io.flavien.demo.domain.permission.service

import io.flavien.demo.domain.group.exception.GroupNotFoundException
import io.flavien.demo.domain.group.repository.GroupRepository
import io.flavien.demo.domain.group.repository.UserGroupRepository
import io.flavien.demo.domain.permission.entity.GroupPermission
import io.flavien.demo.domain.permission.entity.UserPermission
import io.flavien.demo.domain.permission.exception.BadPermissionException
import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.permission.model.PermissionSetting
import io.flavien.demo.domain.permission.model.id.GroupPermissionId
import io.flavien.demo.domain.permission.model.id.UserPermissionId
import io.flavien.demo.domain.permission.repository.GroupPermissionRepository
import io.flavien.demo.domain.permission.repository.UserPermissionRepository
import io.flavien.demo.domain.user.exception.UserNotFoundException
import io.flavien.demo.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PermissionService(
    private val userPermissionRepository: UserPermissionRepository,
    private val userGroupRepository: UserGroupRepository,
    private val groupPermissionRepository: GroupPermissionRepository,
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
) {
    @Transactional(readOnly = true)
    fun getGrantedPermissions(userId: UUID): Set<PermissionEnum> {
        val directDecisions = userPermissionRepository.findByUserId(userId).associate { it.permission to it.allow }
        val groupDecisions = resolveGroupDecisions(userId)

        return PermissionEnum.entries
            .filter { permission -> groupDecisions[permission] ?: directDecisions[permission] ?: false }
            .toSet()
    }

    @Transactional(readOnly = true)
    fun hasPermission(
        userId: UUID,
        permission: PermissionEnum,
    ): Boolean = permission in getGrantedPermissions(userId)

    @Transactional(readOnly = true)
    fun checkPermission(
        userId: UUID,
        permission: PermissionEnum,
    ) {
        if (!hasPermission(userId, permission)) {
            throw BadPermissionException(permission)
        }
    }

    @Transactional
    fun deleteUserPermissions(userId: UUID) {
        userPermissionRepository.deleteByUserId(userId)
    }

    @Transactional(readOnly = true)
    fun getGroupPermissions(groupId: UUID): List<PermissionSetting> {
        val defined = groupPermissionRepository.findByGroupId(groupId).associate { it.permission to it.allow }
        val ancestorDecisions = resolveAncestorDecisions(groupId)
        return PermissionEnum.entries.map { permission ->
            PermissionSetting(
                permission = permission,
                allow = defined[permission],
                locked = ancestorDecisions.containsKey(permission),
                inheritedAllow = ancestorDecisions[permission],
            )
        }
    }

    @Transactional(readOnly = true)
    fun getUserPermissionOverrides(userId: UUID): List<PermissionSetting> {
        val defined = userPermissionRepository.findByUserId(userId).associate { it.permission to it.allow }
        val groupDecisions = resolveGroupDecisions(userId)
        return PermissionEnum.entries.map { permission ->
            PermissionSetting(
                permission = permission,
                allow = defined[permission],
                locked = groupDecisions.containsKey(permission),
                inheritedAllow = groupDecisions[permission],
            )
        }
    }

    @Transactional
    fun setGroupPermission(
        groupId: UUID,
        permission: PermissionEnum,
        allow: Boolean,
    ) {
        val group = groupRepository.findById(groupId).orElseThrow { GroupNotFoundException("Group id $groupId not found") }
        groupPermissionRepository.save(GroupPermission(group, permission, allow))
    }

    @Transactional
    fun removeGroupPermission(
        groupId: UUID,
        permission: PermissionEnum,
    ) {
        groupPermissionRepository.deleteById(GroupPermissionId(groupId, permission))
    }

    @Transactional
    fun setUserPermission(
        userId: UUID,
        permission: PermissionEnum,
        allow: Boolean,
    ) {
        val user = userRepository.getUserById(userId).orElseThrow { UserNotFoundException("User id $userId not found") }
        userPermissionRepository.save(UserPermission(user, permission, allow))
    }

    @Transactional
    fun removeUserPermission(
        userId: UUID,
        permission: PermissionEnum,
    ) {
        userPermissionRepository.deleteById(UserPermissionId(userId, permission))
    }

    private fun resolveGroupDecisions(userId: UUID): Map<PermissionEnum, Boolean> =
        resolveDecisionsForGroups(buildGroupClosure(userGroupRepository.findByUserId(userId).map { it.group.id!! }))

    private fun resolveAncestorDecisions(groupId: UUID): Map<PermissionEnum, Boolean> {
        val parentId =
            groupRepository
                .findById(groupId)
                .orElse(null)
                ?.parent
                ?.id ?: return emptyMap()
        return resolveDecisionsForGroups(buildGroupClosure(listOf(parentId)))
    }

    private fun resolveDecisionsForGroups(depthByGroup: Map<UUID, Int>): Map<PermissionEnum, Boolean> {
        val definitions = mutableMapOf<PermissionEnum, MutableList<Pair<Int, Boolean>>>()
        depthByGroup.forEach { (groupId, depth) ->
            groupPermissionRepository.findByGroupId(groupId).forEach { groupPermission ->
                definitions.getOrPut(groupPermission.permission) { mutableListOf() }.add(depth to groupPermission.allow)
            }
        }

        return definitions.mapValues { (_, candidates) ->
            val rootDepth = candidates.minOf { it.first }
            candidates.filter { it.first == rootDepth }.all { it.second }
        }
    }

    private fun buildGroupClosure(directGroupIds: List<UUID>): Map<UUID, Int> {
        val depthByGroup = mutableMapOf<UUID, Int>()

        for (groupId in directGroupIds) {
            val chain = mutableListOf<UUID>()
            val visited = mutableSetOf<UUID>()
            var current = groupRepository.findById(groupId).orElse(null)
            while (current != null && visited.add(current.id!!)) {
                chain.add(current.id!!)
                current = current.parent
            }

            val rootIndex = chain.size - 1
            chain.forEachIndexed { index, id ->
                val depth = rootIndex - index
                depthByGroup[id] = minOf(depthByGroup[id] ?: depth, depth)
            }
        }

        return depthByGroup
    }
}

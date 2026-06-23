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

@Service
class PermissionService(
    private val userPermissionRepository: UserPermissionRepository,
    private val userGroupRepository: UserGroupRepository,
    private val groupPermissionRepository: GroupPermissionRepository,
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
) {
    /**
     * Resolves the permissions effectively granted to a user.
     *
     * Resolution for a permission `P`:
     *  1. the group closest to the root of the group hierarchy that defines `P` wins (a parent cannot
     *     be overridden by its children, nor by the user); on an equal-depth conflict, deny wins;
     *  2. otherwise a direct [io.flavien.demo.domain.permission.entity.UserPermission] applies (its `allow` value);
     *  3. otherwise `P` is not granted.
     */
    @Transactional(readOnly = true)
    fun getGrantedPermissions(userId: Long): Set<PermissionEnum> {
        val directDecisions = userPermissionRepository.findByUserId(userId).associate { it.permission to it.allow }
        val groupDecisions = resolveGroupDecisions(userId)

        return PermissionEnum.entries
            .filter { permission -> groupDecisions[permission] ?: directDecisions[permission] ?: false }
            .toSet()
    }

    @Transactional(readOnly = true)
    fun hasPermission(
        userId: Long,
        permission: PermissionEnum,
    ): Boolean = permission in getGrantedPermissions(userId)

    @Transactional(readOnly = true)
    fun checkPermission(
        userId: Long,
        permission: PermissionEnum,
    ) {
        if (!hasPermission(userId, permission)) {
            throw BadPermissionException(permission)
        }
    }

    @Transactional
    fun deleteUserPermissions(userId: Long) {
        userPermissionRepository.deleteByUserId(userId)
    }

    @Transactional(readOnly = true)
    fun getGroupPermissions(groupId: Long): List<PermissionSetting> {
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
    fun getUserPermissionOverrides(userId: Long): List<PermissionSetting> {
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
        groupId: Long,
        permission: PermissionEnum,
        allow: Boolean,
    ) {
        val group = groupRepository.findById(groupId).orElseThrow { GroupNotFoundException(groupId) }
        groupPermissionRepository.save(GroupPermission(group, permission, allow))
    }

    @Transactional
    fun removeGroupPermission(
        groupId: Long,
        permission: PermissionEnum,
    ) {
        groupPermissionRepository.deleteById(GroupPermissionId(groupId, permission))
    }

    @Transactional
    fun setUserPermission(
        userId: Long,
        permission: PermissionEnum,
        allow: Boolean,
    ) {
        val user = userRepository.getUserById(userId).orElseThrow { UserNotFoundException(userId) }
        userPermissionRepository.save(UserPermission(user, permission, allow))
    }

    @Transactional
    fun removeUserPermission(
        userId: Long,
        permission: PermissionEnum,
    ) {
        userPermissionRepository.deleteById(UserPermissionId(userId, permission))
    }

    /**
     * Resolves the explicit decisions of every group reachable from the user's memberships, keeping
     * the decision of the group(s) closest to the root. Deny wins when several equal-depth groups
     * disagree. These decisions take priority over the user's own overrides.
     */
    private fun resolveGroupDecisions(userId: Long): Map<PermissionEnum, Boolean> =
        resolveDecisionsForGroups(buildGroupClosure(userGroupRepository.findByUserId(userId).map { it.group.id!! }))

    /**
     * Resolves the explicit decisions of a group's strict ancestors (its parent up to the root).
     * These lock the permission for the group and all of its descendants.
     */
    private fun resolveAncestorDecisions(groupId: Long): Map<PermissionEnum, Boolean> {
        val parentId = groupRepository.findById(groupId).orElse(null)?.parent?.id ?: return emptyMap()
        return resolveDecisionsForGroups(buildGroupClosure(listOf(parentId)))
    }

    /**
     * For each permission explicitly defined within the given group set, keeps the decision of the
     * group(s) closest to the root. Deny wins when several equal-depth groups disagree.
     */
    private fun resolveDecisionsForGroups(depthByGroup: Map<Long, Int>): Map<PermissionEnum, Boolean> {
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

    /**
     * Walks each membership up to its root and returns every reachable group with its depth from the
     * root (root = 0). Cycles are guarded against.
     */
    private fun buildGroupClosure(directGroupIds: List<Long>): Map<Long, Int> {
        val depthByGroup = mutableMapOf<Long, Int>()

        for (groupId in directGroupIds) {
            val chain = mutableListOf<Long>()
            val visited = mutableSetOf<Long>()
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

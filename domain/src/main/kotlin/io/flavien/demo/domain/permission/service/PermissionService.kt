package io.flavien.demo.domain.permission.service

import io.flavien.demo.domain.group.repository.GroupRepository
import io.flavien.demo.domain.group.repository.UserGroupRepository
import io.flavien.demo.domain.permission.exception.BadPermissionException
import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.permission.repository.GroupPermissionRepository
import io.flavien.demo.domain.permission.repository.UserPermissionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PermissionService(
    private val userPermissionRepository: UserPermissionRepository,
    private val userGroupRepository: UserGroupRepository,
    private val groupPermissionRepository: GroupPermissionRepository,
    private val groupRepository: GroupRepository,
) {
    /**
     * Resolves the permissions effectively granted to a user.
     *
     * Resolution for a permission `P`:
     *  1. a direct [io.flavien.demo.domain.permission.entity.UserPermission] wins (its `allow` value);
     *  2. otherwise the group closest to the root of the group hierarchy that defines `P` wins
     *     (a parent cannot be overridden by its children); on an equal-depth conflict, deny wins;
     *  3. otherwise `P` is not granted.
     */
    @Transactional(readOnly = true)
    fun getGrantedPermissions(userId: Long): Set<PermissionEnum> {
        val directDecisions = userPermissionRepository.findByUserId(userId).associate { it.permission to it.allow }
        val groupDecisions = resolveGroupDecisions(userId)

        return PermissionEnum.entries
            .filter { permission -> directDecisions[permission] ?: groupDecisions[permission] ?: false }
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

    /**
     * For each permission explicitly defined within the user's group closure, keeps the decision of
     * the group(s) closest to the root. Deny wins when several equal-depth groups disagree.
     */
    private fun resolveGroupDecisions(userId: Long): Map<PermissionEnum, Boolean> {
        val depthByGroup = buildGroupClosure(userGroupRepository.findByUserId(userId).map { it.group.id!! })

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

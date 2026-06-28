package io.flavien.demo.domain.permission.service

import io.flavien.demo.domain.permission.entity.RolePermission
import io.flavien.demo.domain.permission.entity.UserPermission
import io.flavien.demo.domain.permission.exception.BadPermissionException
import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.permission.model.PermissionSetting
import io.flavien.demo.domain.permission.model.id.RolePermissionId
import io.flavien.demo.domain.permission.model.id.UserPermissionId
import io.flavien.demo.domain.permission.repository.RolePermissionRepository
import io.flavien.demo.domain.permission.repository.UserPermissionRepository
import io.flavien.demo.domain.role.exception.RoleNotFoundException
import io.flavien.demo.domain.role.repository.RoleRepository
import io.flavien.demo.domain.role.repository.UserRoleRepository
import io.flavien.demo.domain.user.exception.UserNotFoundException
import io.flavien.demo.domain.user.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PermissionService(
    private val userPermissionRepository: UserPermissionRepository,
    private val userRoleRepository: UserRoleRepository,
    private val rolePermissionRepository: RolePermissionRepository,
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository,
) {
    @Transactional(readOnly = true)
    fun getGrantedPermissions(userId: UUID): Set<PermissionEnum> {
        val directDecisions = userPermissionRepository.findByUserId(userId).associate { it.permission to it.allow }
        val roleDecisions = resolveRoleDecisions(userId)

        return PermissionEnum.entries
            .filter { permission -> directDecisions[permission] ?: roleDecisions[permission] ?: false }
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
    fun getRolePermissions(roleId: UUID): List<PermissionSetting> {
        val defined = rolePermissionRepository.findByRoleId(roleId).associate { it.permission to it.allow }
        val ancestorDecisions = resolveAncestorDecisions(roleId)
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
    fun getUserPermissionOverrides(
        userId: UUID,
        pageable: Pageable,
    ): Page<PermissionSetting> {
        val defined = userPermissionRepository.findByUserId(userId).associate { it.permission to it.allow }
        val roleDecisions = resolveRoleDecisions(userId)
        val all =
            PermissionEnum.entries.map { permission ->
                PermissionSetting(
                    permission = permission,
                    allow = defined[permission],
                    locked = false,
                    inheritedAllow = roleDecisions[permission],
                )
            }

        val sorted =
            if (pageable.sort.isSorted) {
                all.sortedWith(buildPermissionSettingComparator(pageable.sort))
            } else {
                all
            }

        val total = sorted.size.toLong()
        val from = pageable.offset.toInt().coerceAtMost(sorted.size)
        val to = (from + pageable.pageSize).coerceAtMost(sorted.size)
        val content = if (from < to) sorted.subList(from, to) else emptyList()

        return PageImpl(content, pageable, total)
    }

    private fun buildPermissionSettingComparator(sort: Sort): Comparator<PermissionSetting> {
        var comparator: Comparator<PermissionSetting>? = null
        for (order in sort) {
            val propertyComparator: Comparator<PermissionSetting> =
                when (order.property) {
                    "permission" -> compareBy { it.permission }
                    else -> compareBy { it.permission }
                }
            val directed =
                if (order.isAscending) {
                    propertyComparator
                } else {
                    java.util.Collections.reverseOrder(propertyComparator)
                }
            comparator = comparator?.then(directed) ?: directed
        }
        return comparator ?: compareBy { it.permission }
    }

    @Transactional
    fun setRolePermission(
        roleId: UUID,
        permission: PermissionEnum,
        allow: Boolean,
    ) {
        val role = roleRepository.findById(roleId).orElseThrow { RoleNotFoundException("Role id $roleId not found") }
        rolePermissionRepository.save(RolePermission(role, permission, allow))
    }

    @Transactional
    fun removeRolePermission(
        roleId: UUID,
        permission: PermissionEnum,
    ) {
        rolePermissionRepository.deleteById(RolePermissionId(roleId, permission))
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

    private fun resolveRoleDecisions(userId: UUID): Map<PermissionEnum, Boolean> =
        resolveDecisionsForRoles(buildRoleClosure(userRoleRepository.findByUserId(userId).map { it.role.id!! }))

    private fun resolveAncestorDecisions(roleId: UUID): Map<PermissionEnum, Boolean> {
        val parentId =
            roleRepository
                .findById(roleId)
                .orElse(null)
                ?.parent
                ?.id ?: return emptyMap()
        return resolveDecisionsForRoles(buildRoleClosure(listOf(parentId)))
    }

    private fun resolveDecisionsForRoles(roleIds: Set<UUID>): Map<PermissionEnum, Boolean> {
        val allowed = mutableSetOf<PermissionEnum>()
        roleIds.forEach { roleId ->
            rolePermissionRepository.findByRoleId(roleId).forEach { rolePermission ->
                if (rolePermission.allow) {
                    allowed.add(rolePermission.permission)
                }
            }
        }

        return allowed.associateWith { true }
    }

    private fun buildRoleClosure(directRoleIds: List<UUID>): Set<UUID> {
        val visited = mutableSetOf<UUID>()

        for (roleId in directRoleIds) {
            var current = roleRepository.findById(roleId).orElse(null)
            while (current != null && visited.add(current.id!!)) {
                current = current.parent
            }
        }

        return visited
    }
}

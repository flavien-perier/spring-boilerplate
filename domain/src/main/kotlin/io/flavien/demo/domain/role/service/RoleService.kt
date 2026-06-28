package io.flavien.demo.domain.role.service

import io.flavien.demo.domain.permission.repository.RolePermissionRepository
import io.flavien.demo.domain.role.entity.Role
import io.flavien.demo.domain.role.entity.UserRole
import io.flavien.demo.domain.role.exception.ProtectedRoleException
import io.flavien.demo.domain.role.exception.RoleAlreadyExistsException
import io.flavien.demo.domain.role.exception.RoleHierarchyException
import io.flavien.demo.domain.role.exception.RoleNotFoundException
import io.flavien.demo.domain.role.model.id.UserRoleId
import io.flavien.demo.domain.role.repository.RoleRepository
import io.flavien.demo.domain.role.repository.UserRoleRepository
import io.flavien.demo.domain.user.entity.User
import io.flavien.demo.domain.user.exception.UserNotFoundException
import io.flavien.demo.domain.user.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class RoleService(
    private val roleRepository: RoleRepository,
    private val userRoleRepository: UserRoleRepository,
    private val rolePermissionRepository: RolePermissionRepository,
    private val userRepository: UserRepository,
) {
    @Transactional
    fun assignDefaultRole(user: User) {
        val role =
            roleRepository
                .findByName(DEFAULT_ROLE_NAME)
                .orElseThrow { IllegalStateException("Default role '$DEFAULT_ROLE_NAME' is missing") }

        val id = UserRoleId(user.id!!, role.id!!)
        if (!userRoleRepository.existsById(id)) {
            userRoleRepository.save(UserRole(user, role))
        }
    }

    @Transactional
    fun deleteUserRoles(userId: UUID) {
        userRoleRepository.deleteByUserId(userId)
    }

    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<Role> = roleRepository.findAll(pageable)

    @Transactional(readOnly = true)
    fun getById(id: UUID): Role = roleRepository.findById(id).orElseThrow { RoleNotFoundException("Role id $id not found") }

    @Transactional
    fun create(
        name: String,
        parentId: UUID?,
    ): Role {
        if (roleRepository.existsByName(name)) {
            throw RoleAlreadyExistsException(name)
        }

        val parent = parentId?.let { getById(it) }

        return roleRepository.save(Role(name = name, parent = parent))
    }

    @Transactional
    fun update(
        id: UUID,
        name: String?,
        parentId: UUID?,
    ): Role {
        val role = getById(id)

        if (name != null && name != role.name && roleRepository.existsByName(name)) {
            throw RoleAlreadyExistsException(name)
        }

        val parent =
            if (parentId != null) {
                if (parentId == id) {
                    throw RoleHierarchyException(id, parentId)
                }

                val resolvedParent = getById(parentId)

                val visited = mutableSetOf<UUID>()
                var ancestor: Role? = resolvedParent
                while (ancestor != null && visited.add(ancestor.id!!)) {
                    if (ancestor.id == id) {
                        throw RoleHierarchyException(id, parentId)
                    }
                    ancestor = ancestor.parent
                }

                resolvedParent
            } else {
                null
            }

        if (name != null) role.name = name
        role.parent = parent

        return roleRepository.save(role)
    }

    @Transactional
    fun delete(id: UUID) {
        val role = getById(id)

        if (role.name in setOf(DEFAULT_ROLE_NAME, ADMIN_ROLE_NAME)) {
            throw ProtectedRoleException(role.name)
        }

        if (roleRepository.existsByParentId(id)) {
            throw RoleHierarchyException(id)
        }

        rolePermissionRepository.deleteByRoleId(id)
        userRoleRepository.deleteByRoleId(id)
        roleRepository.deleteById(id)
    }

    @Transactional
    fun addUserToRole(
        userId: UUID,
        roleId: UUID,
    ) {
        val user = userRepository.getUserById(userId).orElseThrow { UserNotFoundException("User id $userId not found") }
        val role = getById(roleId)

        val id = UserRoleId(user.id!!, role.id!!)
        if (!userRoleRepository.existsById(id)) {
            userRoleRepository.save(UserRole(user, role))
        }
    }

    @Transactional
    fun removeUserFromRole(
        userId: UUID,
        roleId: UUID,
    ) {
        userRoleRepository.deleteById(UserRoleId(userId, roleId))
    }

    @Transactional(readOnly = true)
    fun getUserRoles(
        userId: UUID,
        pageable: Pageable,
    ): Page<Role> = userRoleRepository.findByUserId(userId, mapSortToRoleAssociation(pageable)).map { it.role }

    private fun mapSortToRoleAssociation(pageable: Pageable): Pageable {
        if (pageable.sort.isUnsorted) {
            return pageable
        }
        val mappedSort =
            Sort.by(
                pageable.sort
                    .map { order ->
                        Sort.Order
                            .by("role.${order.property}")
                            .with(order.direction)
                            .with(order.nullHandling)
                            .let { if (order.isIgnoreCase) it.ignoreCase() else it }
                    }.toList(),
            )
        return PageRequest.of(pageable.pageNumber, pageable.pageSize, mappedSort)
    }

    companion object {
        const val DEFAULT_ROLE_NAME = "USER"
        const val ADMIN_ROLE_NAME = "ADMIN"
    }
}

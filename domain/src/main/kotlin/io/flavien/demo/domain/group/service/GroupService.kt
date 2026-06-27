package io.flavien.demo.domain.group.service

import io.flavien.demo.domain.group.entity.Group
import io.flavien.demo.domain.group.entity.UserGroup
import io.flavien.demo.domain.group.exception.GroupAlreadyExistsException
import io.flavien.demo.domain.group.exception.GroupHierarchyException
import io.flavien.demo.domain.group.exception.GroupNotFoundException
import io.flavien.demo.domain.group.exception.ProtectedGroupException
import io.flavien.demo.domain.group.model.id.UserGroupId
import io.flavien.demo.domain.group.repository.GroupRepository
import io.flavien.demo.domain.group.repository.UserGroupRepository
import io.flavien.demo.domain.permission.repository.GroupPermissionRepository
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
class GroupService(
    private val groupRepository: GroupRepository,
    private val userGroupRepository: UserGroupRepository,
    private val groupPermissionRepository: GroupPermissionRepository,
    private val userRepository: UserRepository,
) {
    @Transactional
    fun assignDefaultGroup(user: User) {
        val group =
            groupRepository
                .findByName(DEFAULT_GROUP_NAME)
                .orElseThrow { IllegalStateException("Default group '$DEFAULT_GROUP_NAME' is missing") }

        val id = UserGroupId(user.id!!, group.id!!)
        if (!userGroupRepository.existsById(id)) {
            userGroupRepository.save(UserGroup(user, group))
        }
    }

    @Transactional
    fun deleteUserGroups(userId: UUID) {
        userGroupRepository.deleteByUserId(userId)
    }

    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<Group> = groupRepository.findAll(pageable)

    @Transactional(readOnly = true)
    fun getById(id: UUID): Group = groupRepository.findById(id).orElseThrow { GroupNotFoundException("Group id $id not found") }

    @Transactional
    fun create(
        name: String,
        parentId: UUID?,
    ): Group {
        if (groupRepository.existsByName(name)) {
            throw GroupAlreadyExistsException(name)
        }

        val parent = parentId?.let { getById(it) }

        return groupRepository.save(Group(name = name, parent = parent))
    }

    @Transactional
    fun update(
        id: UUID,
        name: String?,
        parentId: UUID?,
    ): Group {
        val group = getById(id)

        if (name != null && name != group.name && groupRepository.existsByName(name)) {
            throw GroupAlreadyExistsException(name)
        }

        val parent =
            if (parentId != null) {
                if (parentId == id) {
                    throw GroupHierarchyException(id, parentId)
                }

                val resolvedParent = getById(parentId)

                val visited = mutableSetOf<UUID>()
                var ancestor: Group? = resolvedParent
                while (ancestor != null && visited.add(ancestor.id!!)) {
                    if (ancestor.id == id) {
                        throw GroupHierarchyException(id, parentId)
                    }
                    ancestor = ancestor.parent
                }

                resolvedParent
            } else {
                null
            }

        if (name != null) group.name = name
        group.parent = parent

        return groupRepository.save(group)
    }

    @Transactional
    fun delete(id: UUID) {
        val group = getById(id)

        if (group.name in setOf(DEFAULT_GROUP_NAME, ADMIN_GROUP_NAME)) {
            throw ProtectedGroupException(group.name)
        }

        if (groupRepository.existsByParentId(id)) {
            throw GroupHierarchyException(id)
        }

        groupPermissionRepository.deleteByGroupId(id)
        userGroupRepository.deleteByGroupId(id)
        groupRepository.deleteById(id)
    }

    @Transactional
    fun addUserToGroup(
        userId: UUID,
        groupId: UUID,
    ) {
        val user = userRepository.getUserById(userId).orElseThrow { UserNotFoundException("User id $userId not found") }
        val group = getById(groupId)

        val id = UserGroupId(user.id!!, group.id!!)
        if (!userGroupRepository.existsById(id)) {
            userGroupRepository.save(UserGroup(user, group))
        }
    }

    @Transactional
    fun removeUserFromGroup(
        userId: UUID,
        groupId: UUID,
    ) {
        userGroupRepository.deleteById(UserGroupId(userId, groupId))
    }

    @Transactional(readOnly = true)
    fun getUserGroups(
        userId: UUID,
        pageable: Pageable,
    ): Page<Group> = userGroupRepository.findByUserId(userId, mapSortToGroupAssociation(pageable)).map { it.group }

    private fun mapSortToGroupAssociation(pageable: Pageable): Pageable {
        if (pageable.sort.isUnsorted) {
            return pageable
        }
        val mappedSort =
            Sort.by(
                pageable.sort
                    .map { order ->
                        Sort.Order
                            .by("group.${order.property}")
                            .with(order.direction)
                            .with(order.nullHandling)
                            .let { if (order.isIgnoreCase) it.ignoreCase() else it }
                    }.toList(),
            )
        return PageRequest.of(pageable.pageNumber, pageable.pageSize, mappedSort)
    }

    companion object {
        const val DEFAULT_GROUP_NAME = "USER"
        const val ADMIN_GROUP_NAME = "ADMIN"
    }
}

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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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
    fun deleteUserGroups(userId: Long) {
        userGroupRepository.deleteByUserId(userId)
    }

    @Transactional(readOnly = true)
    fun findAll(): List<Group> = groupRepository.findAll()

    @Transactional(readOnly = true)
    fun get(id: Long): Group = groupRepository.findById(id).orElseThrow { GroupNotFoundException(id) }

    @Transactional
    fun create(
        name: String,
        parentId: Long?,
    ): Group {
        if (groupRepository.existsByName(name)) {
            throw GroupAlreadyExistsException(name)
        }

        val parent = parentId?.let { get(it) }

        return groupRepository.save(Group(name = name, parent = parent))
    }

    @Transactional
    fun update(
        id: Long,
        name: String?,
        parentId: Long?,
    ): Group {
        val group = get(id)

        if (name != null && name != group.name && groupRepository.existsByName(name)) {
            throw GroupAlreadyExistsException(name)
        }

        val parent =
            if (parentId != null) {
                if (parentId == id) {
                    throw GroupHierarchyException(id, parentId)
                }

                val resolvedParent = get(parentId)

                val visited = mutableSetOf<Long>()
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
    fun delete(id: Long) {
        val group = get(id)

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
        userId: Long,
        groupId: Long,
    ) {
        val user = userRepository.getUserById(userId).orElseThrow { UserNotFoundException(userId) }
        val group = get(groupId)

        val id = UserGroupId(user.id!!, group.id!!)
        if (!userGroupRepository.existsById(id)) {
            userGroupRepository.save(UserGroup(user, group))
        }
    }

    @Transactional
    fun removeUserFromGroup(
        userId: Long,
        groupId: Long,
    ) {
        userGroupRepository.deleteById(UserGroupId(userId, groupId))
    }

    @Transactional(readOnly = true)
    fun getUserGroups(userId: Long): List<Group> = userGroupRepository.findByUserId(userId).map { it.group }

    companion object {
        const val DEFAULT_GROUP_NAME = "USER"
        const val ADMIN_GROUP_NAME = "ADMIN"
    }
}

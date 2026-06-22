package io.flavien.demo.domain.group.service

import io.flavien.demo.domain.group.entity.UserGroup
import io.flavien.demo.domain.group.model.id.UserGroupId
import io.flavien.demo.domain.group.repository.GroupRepository
import io.flavien.demo.domain.group.repository.UserGroupRepository
import io.flavien.demo.domain.user.entity.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GroupService(
    private val groupRepository: GroupRepository,
    private val userGroupRepository: UserGroupRepository,
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

    companion object {
        const val DEFAULT_GROUP_NAME = "USER"
    }
}

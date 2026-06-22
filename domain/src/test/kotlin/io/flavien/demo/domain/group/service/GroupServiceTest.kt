package io.flavien.demo.domain.group.service

import io.flavien.demo.domain.group.GroupTestFactory
import io.flavien.demo.domain.group.entity.UserGroup
import io.flavien.demo.domain.group.model.id.UserGroupId
import io.flavien.demo.domain.group.repository.GroupRepository
import io.flavien.demo.domain.group.repository.UserGroupRepository
import io.flavien.demo.domain.user.UserTestFactory
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class GroupServiceTest {
    @InjectMocks
    var groupService: GroupService? = null

    @Mock
    var groupRepository: GroupRepository? = null

    @Mock
    var userGroupRepository: UserGroupRepository? = null

    @Test
    fun `Should assign the default group when the user has none`() {
        // Given
        val user = UserTestFactory.initUser(id = 1L)
        val group = GroupTestFactory.initGroup(id = 10L, name = "USER")

        Mockito
            .`when`(groupRepository!!.findByName("USER"))
            .thenReturn(Optional.of(group))
        Mockito
            .`when`(userGroupRepository!!.existsById(UserGroupId(user.id!!, group.id!!)))
            .thenReturn(false)

        // When
        groupService!!.assignDefaultGroup(user)

        // Then
        Mockito.verify(groupRepository!!).findByName("USER")
        Mockito.verify(userGroupRepository!!).existsById(UserGroupId(user.id!!, group.id!!))
        Mockito.verify(userGroupRepository!!).save(UserGroup(user, group))
    }

    @Test
    fun `Should not re-assign the default group when already present`() {
        // Given
        val user = UserTestFactory.initUser(id = 1L)
        val group = GroupTestFactory.initGroup(id = 10L, name = "USER")

        Mockito
            .`when`(groupRepository!!.findByName("USER"))
            .thenReturn(Optional.of(group))
        Mockito
            .`when`(userGroupRepository!!.existsById(UserGroupId(user.id!!, group.id!!)))
            .thenReturn(true)

        // When
        groupService!!.assignDefaultGroup(user)

        // Then
        Mockito.verify(groupRepository!!).findByName("USER")
        Mockito.verify(userGroupRepository!!).existsById(UserGroupId(user.id!!, group.id!!))
        Mockito.verify(userGroupRepository!!, Mockito.never()).save(any())
    }

    @Test
    fun `Should fail when the default group is missing`() {
        // Given
        val user = UserTestFactory.initUser(id = 1L)

        Mockito
            .`when`(groupRepository!!.findByName("USER"))
            .thenReturn(Optional.empty())

        // When/Then
        assertThrows(IllegalStateException::class.java) {
            groupService!!.assignDefaultGroup(user)
        }

        Mockito.verify(groupRepository!!).findByName("USER")
        Mockito.verify(userGroupRepository!!, Mockito.never()).save(any())
    }
}

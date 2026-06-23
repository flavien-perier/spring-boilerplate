package io.flavien.demo.domain.group.service

import io.flavien.demo.domain.group.GroupTestFactory
import io.flavien.demo.domain.group.entity.UserGroup
import io.flavien.demo.domain.group.exception.GroupAlreadyExistsException
import io.flavien.demo.domain.group.exception.GroupHierarchyException
import io.flavien.demo.domain.group.exception.GroupNotFoundException
import io.flavien.demo.domain.group.exception.ProtectedGroupException
import io.flavien.demo.domain.group.model.id.UserGroupId
import io.flavien.demo.domain.group.repository.GroupRepository
import io.flavien.demo.domain.group.repository.UserGroupRepository
import io.flavien.demo.domain.permission.repository.GroupPermissionRepository
import io.flavien.demo.domain.user.UserTestFactory
import io.flavien.demo.domain.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
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

    @Mock
    var groupPermissionRepository: GroupPermissionRepository? = null

    @Mock
    var userRepository: UserRepository? = null

    @Test
    fun `Should assign the default group when the user has none`() {
        val user = UserTestFactory.initUser(id = 1L)
        val group = GroupTestFactory.initGroup(id = 10L, name = "USER")

        Mockito
            .`when`(groupRepository!!.findByName("USER"))
            .thenReturn(Optional.of(group))
        Mockito
            .`when`(userGroupRepository!!.existsById(UserGroupId(user.id!!, group.id!!)))
            .thenReturn(false)

        groupService!!.assignDefaultGroup(user)

        Mockito.verify(groupRepository!!).findByName("USER")
        Mockito.verify(userGroupRepository!!).existsById(UserGroupId(user.id!!, group.id!!))
        Mockito.verify(userGroupRepository!!).save(UserGroup(user, group))
    }

    @Test
    fun `Should not re-assign the default group when already present`() {
        val user = UserTestFactory.initUser(id = 1L)
        val group = GroupTestFactory.initGroup(id = 10L, name = "USER")

        Mockito
            .`when`(groupRepository!!.findByName("USER"))
            .thenReturn(Optional.of(group))
        Mockito
            .`when`(userGroupRepository!!.existsById(UserGroupId(user.id!!, group.id!!)))
            .thenReturn(true)

        groupService!!.assignDefaultGroup(user)

        Mockito.verify(groupRepository!!).findByName("USER")
        Mockito.verify(userGroupRepository!!).existsById(UserGroupId(user.id!!, group.id!!))
        Mockito.verify(userGroupRepository!!, Mockito.never()).save(any())
    }

    @Test
    fun `Should fail when the default group is missing`() {
        val user = UserTestFactory.initUser(id = 1L)

        Mockito
            .`when`(groupRepository!!.findByName("USER"))
            .thenReturn(Optional.empty())

        assertThrows(IllegalStateException::class.java) {
            groupService!!.assignDefaultGroup(user)
        }

        Mockito.verify(groupRepository!!).findByName("USER")
        Mockito.verify(userGroupRepository!!, Mockito.never()).save(any())
    }

    @Test
    fun `Should find all groups`() {
        val group1 = GroupTestFactory.initGroup(id = 1L, name = "G1")
        val group2 = GroupTestFactory.initGroup(id = 2L, name = "G2")

        Mockito.`when`(groupRepository!!.findAll()).thenReturn(listOf(group1, group2))

        val result = groupService!!.findAll()

        assertThat(result).containsExactly(group1, group2)
    }

    @Test
    fun `Should get group by id`() {
        val group = GroupTestFactory.initGroup(id = 1L, name = "G")

        Mockito.`when`(groupRepository!!.findById(1L)).thenReturn(Optional.of(group))

        val result = groupService!!.get(1L)

        assertThat(result).isEqualTo(group)
    }

    @Test
    fun `Should throw GroupNotFoundException when group not found`() {
        Mockito.`when`(groupRepository!!.findById(1L)).thenReturn(Optional.empty())

        assertThrows(GroupNotFoundException::class.java) {
            groupService!!.get(1L)
        }
    }

    @Test
    fun `Should create a group`() {
        val saved = GroupTestFactory.initGroup(id = 1L, name = "NEW")
        val parent = GroupTestFactory.initGroup(id = 10L, name = "PARENT")

        Mockito.`when`(groupRepository!!.existsByName("NEW")).thenReturn(false)
        Mockito.`when`(groupRepository!!.findById(10L)).thenReturn(Optional.of(parent))
        Mockito.`when`(groupRepository!!.save(any())).thenReturn(saved)

        val result = groupService!!.create("NEW", 10L)

        assertThat(result.id).isEqualTo(1L)
        assertThat(result.name).isEqualTo("NEW")
        Mockito.verify(groupRepository!!).existsByName("NEW")
    }

    @Test
    fun `Should fail to create a group with duplicate name`() {
        Mockito.`when`(groupRepository!!.existsByName("DUP")).thenReturn(true)

        assertThrows(GroupAlreadyExistsException::class.java) {
            groupService!!.create("DUP", null)
        }
    }

    @Test
    fun `Should update a group name`() {
        val existing = GroupTestFactory.initGroup(id = 1L, name = "OLD")
        val updated = GroupTestFactory.initGroup(id = 1L, name = "NEW")

        Mockito.`when`(groupRepository!!.findById(1L)).thenReturn(Optional.of(existing))
        Mockito.`when`(groupRepository!!.existsByName("NEW")).thenReturn(false)
        Mockito.`when`(groupRepository!!.save(any())).thenReturn(updated)

        val result = groupService!!.update(1L, "NEW", null)

        assertThat(result.name).isEqualTo("NEW")
    }

    @Test
    fun `Should remove the parent when parentId is null`() {
        val parent = GroupTestFactory.initGroup(id = 10L, name = "PARENT")
        val existing = GroupTestFactory.initGroup(id = 1L, name = "CHILD", parent = parent)

        Mockito.`when`(groupRepository!!.findById(1L)).thenReturn(Optional.of(existing))
        Mockito.`when`(groupRepository!!.save(any())).thenReturn(existing)

        groupService!!.update(1L, null, null)

        assertThat(existing.parent).isNull()
    }

    @Test
    fun `Should fail to update with duplicate name`() {
        val existing = GroupTestFactory.initGroup(id = 1L, name = "OLD")

        Mockito.`when`(groupRepository!!.findById(1L)).thenReturn(Optional.of(existing))
        Mockito.`when`(groupRepository!!.existsByName("DUP")).thenReturn(true)

        assertThrows(GroupAlreadyExistsException::class.java) {
            groupService!!.update(1L, "DUP", null)
        }
    }

    @Test
    fun `Should reject setting parentId to itself`() {
        val group = GroupTestFactory.initGroup(id = 1L, name = "G")

        Mockito.`when`(groupRepository!!.findById(1L)).thenReturn(Optional.of(group))

        assertThrows(GroupHierarchyException::class.java) {
            groupService!!.update(1L, null, 1L)
        }
    }

    @Test
    fun `Should reject setting parentId to a descendant`() {
        val grandchild = GroupTestFactory.initGroup(id = 1L, name = "GC")
        val child = GroupTestFactory.initGroup(id = 2L, name = "C", parent = grandchild)

        Mockito.`when`(groupRepository!!.findById(1L)).thenReturn(Optional.of(grandchild))
        Mockito.`when`(groupRepository!!.findById(2L)).thenReturn(Optional.of(child))

        assertThrows(GroupHierarchyException::class.java) {
            groupService!!.update(1L, null, 2L)
        }
    }

    @Test
    fun `Should delete a group`() {
        val group = GroupTestFactory.initGroup(id = 1L, name = "CUSTOM")

        Mockito.`when`(groupRepository!!.findById(1L)).thenReturn(Optional.of(group))
        Mockito.`when`(groupRepository!!.existsByParentId(1L)).thenReturn(false)

        groupService!!.delete(1L)

        Mockito.verify(groupPermissionRepository!!).deleteByGroupId(1L)
        Mockito.verify(userGroupRepository!!).deleteByGroupId(1L)
        Mockito.verify(groupRepository!!).deleteById(1L)
    }

    @Test
    fun `Should reject delete of protected USER group`() {
        val group = GroupTestFactory.initGroup(id = 1L, name = "USER")

        Mockito.`when`(groupRepository!!.findById(1L)).thenReturn(Optional.of(group))

        assertThrows(ProtectedGroupException::class.java) {
            groupService!!.delete(1L)
        }
    }

    @Test
    fun `Should reject delete of protected ADMIN group`() {
        val group = GroupTestFactory.initGroup(id = 1L, name = "ADMIN")

        Mockito.`when`(groupRepository!!.findById(1L)).thenReturn(Optional.of(group))

        assertThrows(ProtectedGroupException::class.java) {
            groupService!!.delete(1L)
        }
    }

    @Test
    fun `Should reject delete of group with children`() {
        val group = GroupTestFactory.initGroup(id = 1L, name = "PARENT")

        Mockito.`when`(groupRepository!!.findById(1L)).thenReturn(Optional.of(group))
        Mockito.`when`(groupRepository!!.existsByParentId(1L)).thenReturn(true)

        assertThrows(GroupHierarchyException::class.java) {
            groupService!!.delete(1L)
        }
    }

    @Test
    fun `Should add user to group`() {
        val user = UserTestFactory.initUser(id = 1L)
        val group = GroupTestFactory.initGroup(id = 10L, name = "G")

        Mockito.`when`(userRepository!!.getUserById(1L)).thenReturn(Optional.of(user))
        Mockito.`when`(groupRepository!!.findById(10L)).thenReturn(Optional.of(group))
        Mockito.`when`(userGroupRepository!!.existsById(UserGroupId(1L, 10L))).thenReturn(false)

        groupService!!.addUserToGroup(1L, 10L)

        Mockito.verify(userGroupRepository!!).save(UserGroup(user, group))
    }

    @Test
    fun `Should not duplicate user group membership`() {
        val user = UserTestFactory.initUser(id = 1L)
        val group = GroupTestFactory.initGroup(id = 10L, name = "G")

        Mockito.`when`(userRepository!!.getUserById(1L)).thenReturn(Optional.of(user))
        Mockito.`when`(groupRepository!!.findById(10L)).thenReturn(Optional.of(group))
        Mockito.`when`(userGroupRepository!!.existsById(UserGroupId(1L, 10L))).thenReturn(true)

        groupService!!.addUserToGroup(1L, 10L)

        Mockito.verify(userGroupRepository!!, Mockito.never()).save(any())
    }

    @Test
    fun `Should remove user from group`() {
        groupService!!.removeUserFromGroup(1L, 10L)

        Mockito.verify(userGroupRepository!!).deleteById(UserGroupId(1L, 10L))
    }

    @Test
    fun `Should get user groups`() {
        val group1 = GroupTestFactory.initGroup(id = 1L, name = "G1")
        val userGroup = GroupTestFactory.initUserGroup(group = group1)

        Mockito.`when`(userGroupRepository!!.findByUserId(1L)).thenReturn(listOf(userGroup))

        val result = groupService!!.getUserGroups(1L)

        assertThat(result).containsExactly(group1)
    }
}

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
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.util.Optional
import java.util.UUID

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

    private val userId = UUID.fromString("00000000-0000-0000-0000-00000000000a")
    private val group1Id = UUID.fromString("00000000-0000-0000-0000-000000000001")
    private val group2Id = UUID.fromString("00000000-0000-0000-0000-000000000002")
    private val group10Id = UUID.fromString("00000000-0000-0000-0000-000000000010")

    // Kotlin-safe Mockito matcher helpers: the stock matchers return null, which the compiler
    // rejects when the result is passed to a non-null parameter such as findByUserId(UUID, Pageable).
    // Returning a real non-null value works because Mockito matches on the registered matcher
    // (pushed onto its internal stack), not on the dummy value passed at the call site.
    private fun <T> eqArg(value: T): T {
        eq(value)
        return value
    }

    private fun anyPageable(): Pageable {
        any<Pageable>()
        return Pageable.unpaged()
    }

    private fun capturePageable(captor: ArgumentCaptor<Pageable>): Pageable {
        captor.capture()
        return Pageable.unpaged()
    }

    @Test
    fun `Should assign the default group when the user has none`() {
        val user = UserTestFactory.initUser(id = userId)
        val group = GroupTestFactory.initGroup(id = group10Id, name = "USER")

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
        val user = UserTestFactory.initUser(id = userId)
        val group = GroupTestFactory.initGroup(id = group10Id, name = "USER")

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
        val user = UserTestFactory.initUser(id = userId)

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
        val g1 = GroupTestFactory.initGroup(id = group1Id, name = "G1")
        val g2 = GroupTestFactory.initGroup(id = group2Id, name = "G2")
        val pageable = PageRequest.of(0, 10)

        Mockito.`when`(groupRepository!!.findAll(pageable)).thenReturn(PageImpl(listOf(g1, g2), pageable, 2))

        val result = groupService!!.findAll(pageable)

        assertThat(result.content).containsExactly(g1, g2)
        assertThat(result.totalElements).isEqualTo(2)
        assertThat(result.totalPages).isEqualTo(1)
    }

    @Test
    fun `Should get group by id`() {
        val group = GroupTestFactory.initGroup(id = group1Id, name = "G")

        Mockito.`when`(groupRepository!!.findById(group1Id)).thenReturn(Optional.of(group))

        val result = groupService!!.getById(group1Id)

        assertThat(result).isEqualTo(group)
    }

    @Test
    fun `Should throw GroupNotFoundException when group not found`() {
        Mockito.`when`(groupRepository!!.findById(group1Id)).thenReturn(Optional.empty())

        assertThrows(GroupNotFoundException::class.java) {
            groupService!!.getById(group1Id)
        }
    }

    @Test
    fun `Should create a group`() {
        val saved = GroupTestFactory.initGroup(id = group1Id, name = "NEW")
        val parent = GroupTestFactory.initGroup(id = group10Id, name = "PARENT")

        Mockito.`when`(groupRepository!!.existsByName("NEW")).thenReturn(false)
        Mockito.`when`(groupRepository!!.findById(group10Id)).thenReturn(Optional.of(parent))
        Mockito.`when`(groupRepository!!.save(any())).thenReturn(saved)

        val result = groupService!!.create("NEW", group10Id)

        assertThat(result.id).isEqualTo(group1Id)
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
        val existing = GroupTestFactory.initGroup(id = group1Id, name = "OLD")
        val updated = GroupTestFactory.initGroup(id = group1Id, name = "NEW")

        Mockito.`when`(groupRepository!!.findById(group1Id)).thenReturn(Optional.of(existing))
        Mockito.`when`(groupRepository!!.existsByName("NEW")).thenReturn(false)
        Mockito.`when`(groupRepository!!.save(any())).thenReturn(updated)

        val result = groupService!!.update(group1Id, "NEW", null)

        assertThat(result.name).isEqualTo("NEW")
    }

    @Test
    fun `Should remove the parent when parentId is null`() {
        val parent = GroupTestFactory.initGroup(id = group10Id, name = "PARENT")
        val existing = GroupTestFactory.initGroup(id = group1Id, name = "CHILD", parent = parent)

        Mockito.`when`(groupRepository!!.findById(group1Id)).thenReturn(Optional.of(existing))
        Mockito.`when`(groupRepository!!.save(any())).thenReturn(existing)

        groupService!!.update(group1Id, null, null)

        assertThat(existing.parent).isNull()
    }

    @Test
    fun `Should fail to update with duplicate name`() {
        val existing = GroupTestFactory.initGroup(id = group1Id, name = "OLD")

        Mockito.`when`(groupRepository!!.findById(group1Id)).thenReturn(Optional.of(existing))
        Mockito.`when`(groupRepository!!.existsByName("DUP")).thenReturn(true)

        assertThrows(GroupAlreadyExistsException::class.java) {
            groupService!!.update(group1Id, "DUP", null)
        }
    }

    @Test
    fun `Should reject setting parentId to itself`() {
        val group = GroupTestFactory.initGroup(id = group1Id, name = "G")

        Mockito.`when`(groupRepository!!.findById(group1Id)).thenReturn(Optional.of(group))

        assertThrows(GroupHierarchyException::class.java) {
            groupService!!.update(group1Id, null, group1Id)
        }
    }

    @Test
    fun `Should reject setting parentId to a descendant`() {
        val grandchild = GroupTestFactory.initGroup(id = group1Id, name = "GC")
        val child = GroupTestFactory.initGroup(id = group2Id, name = "C", parent = grandchild)

        Mockito.`when`(groupRepository!!.findById(group1Id)).thenReturn(Optional.of(grandchild))
        Mockito.`when`(groupRepository!!.findById(group2Id)).thenReturn(Optional.of(child))

        assertThrows(GroupHierarchyException::class.java) {
            groupService!!.update(group1Id, null, group2Id)
        }
    }

    @Test
    fun `Should delete a group`() {
        val group = GroupTestFactory.initGroup(id = group1Id, name = "CUSTOM")

        Mockito.`when`(groupRepository!!.findById(group1Id)).thenReturn(Optional.of(group))
        Mockito.`when`(groupRepository!!.existsByParentId(group1Id)).thenReturn(false)

        groupService!!.delete(group1Id)

        Mockito.verify(groupPermissionRepository!!).deleteByGroupId(group1Id)
        Mockito.verify(userGroupRepository!!).deleteByGroupId(group1Id)
        Mockito.verify(groupRepository!!).deleteById(group1Id)
    }

    @Test
    fun `Should reject delete of protected USER group`() {
        val group = GroupTestFactory.initGroup(id = group1Id, name = "USER")

        Mockito.`when`(groupRepository!!.findById(group1Id)).thenReturn(Optional.of(group))

        assertThrows(ProtectedGroupException::class.java) {
            groupService!!.delete(group1Id)
        }
    }

    @Test
    fun `Should reject delete of protected ADMIN group`() {
        val group = GroupTestFactory.initGroup(id = group1Id, name = "ADMIN")

        Mockito.`when`(groupRepository!!.findById(group1Id)).thenReturn(Optional.of(group))

        assertThrows(ProtectedGroupException::class.java) {
            groupService!!.delete(group1Id)
        }
    }

    @Test
    fun `Should reject delete of group with children`() {
        val group = GroupTestFactory.initGroup(id = group1Id, name = "PARENT")

        Mockito.`when`(groupRepository!!.findById(group1Id)).thenReturn(Optional.of(group))
        Mockito.`when`(groupRepository!!.existsByParentId(group1Id)).thenReturn(true)

        assertThrows(GroupHierarchyException::class.java) {
            groupService!!.delete(group1Id)
        }
    }

    @Test
    fun `Should add user to group`() {
        val user = UserTestFactory.initUser(id = userId)
        val group = GroupTestFactory.initGroup(id = group10Id, name = "G")

        Mockito.`when`(userRepository!!.getUserById(userId)).thenReturn(Optional.of(user))
        Mockito.`when`(groupRepository!!.findById(group10Id)).thenReturn(Optional.of(group))
        Mockito.`when`(userGroupRepository!!.existsById(UserGroupId(userId, group10Id))).thenReturn(false)

        groupService!!.addUserToGroup(userId, group10Id)

        Mockito.verify(userGroupRepository!!).save(UserGroup(user, group))
    }

    @Test
    fun `Should not duplicate user group membership`() {
        val user = UserTestFactory.initUser(id = userId)
        val group = GroupTestFactory.initGroup(id = group10Id, name = "G")

        Mockito.`when`(userRepository!!.getUserById(userId)).thenReturn(Optional.of(user))
        Mockito.`when`(groupRepository!!.findById(group10Id)).thenReturn(Optional.of(group))
        Mockito.`when`(userGroupRepository!!.existsById(UserGroupId(userId, group10Id))).thenReturn(true)

        groupService!!.addUserToGroup(userId, group10Id)

        Mockito.verify(userGroupRepository!!, Mockito.never()).save(any())
    }

    @Test
    fun `Should remove user from group`() {
        groupService!!.removeUserFromGroup(userId, group10Id)

        Mockito.verify(userGroupRepository!!).deleteById(UserGroupId(userId, group10Id))
    }

    @Test
    fun `Should get user groups`() {
        val group1 = GroupTestFactory.initGroup(id = group1Id, name = "G1")
        val userGroup = GroupTestFactory.initUserGroup(group = group1)
        val pageable = PageRequest.of(0, 10)

        Mockito
            .`when`(userGroupRepository!!.findByUserId(userId, pageable))
            .thenReturn(PageImpl(listOf(userGroup), pageable, 1))

        val result = groupService!!.getUserGroups(userId, pageable)

        assertThat(result.content).containsExactly(group1)
        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.totalPages).isEqualTo(1)
    }

    @Test
    fun `Should remap sort by name ascending onto the group association when getting user groups`() {
        val group1 = GroupTestFactory.initGroup(id = group1Id, name = "G1")
        val userGroup = GroupTestFactory.initUserGroup(group = group1)
        val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"))
        val pageableCaptor = ArgumentCaptor.forClass(Pageable::class.java)

        Mockito
            .`when`(userGroupRepository!!.findByUserId(eqArg(userId), anyPageable()))
            .thenReturn(PageImpl(listOf(userGroup), PageRequest.of(0, 10), 1))

        val result = groupService!!.getUserGroups(userId, pageable)

        Mockito.verify(userGroupRepository!!).findByUserId(eqArg(userId), capturePageable(pageableCaptor))
        val captured = pageableCaptor.value
        val order = captured.sort.getOrderFor("group.name")
        assertThat(order).isNotNull()
        assertThat(order!!.direction).isEqualTo(Sort.Direction.ASC)
        assertThat(captured.sort.getOrderFor("name")).isNull()
        assertThat(captured.pageNumber).isEqualTo(0)
        assertThat(captured.pageSize).isEqualTo(10)
        assertThat(result.content).containsExactly(group1)
    }

    @Test
    fun `Should remap sort by name descending onto the group association when getting user groups`() {
        val group1 = GroupTestFactory.initGroup(id = group1Id, name = "G1")
        val group2 = GroupTestFactory.initGroup(id = group2Id, name = "G2")
        val userGroup1 = GroupTestFactory.initUserGroup(group = group1)
        val userGroup2 = GroupTestFactory.initUserGroup(group = group2)
        val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name"))
        val pageableCaptor = ArgumentCaptor.forClass(Pageable::class.java)

        Mockito
            .`when`(userGroupRepository!!.findByUserId(eqArg(userId), anyPageable()))
            .thenReturn(PageImpl(listOf(userGroup2, userGroup1), PageRequest.of(0, 10), 2))

        val result = groupService!!.getUserGroups(userId, pageable)

        Mockito.verify(userGroupRepository!!).findByUserId(eqArg(userId), capturePageable(pageableCaptor))
        val captured = pageableCaptor.value
        val order = captured.sort.getOrderFor("group.name")
        assertThat(order).isNotNull()
        assertThat(order!!.direction).isEqualTo(Sort.Direction.DESC)
        assertThat(captured.sort.getOrderFor("name")).isNull()
        assertThat(result.content).containsExactly(group2, group1)
    }

    @Test
    fun `Should leave the pageable unsorted when getting user groups without a sort`() {
        val group1 = GroupTestFactory.initGroup(id = group1Id, name = "G1")
        val userGroup = GroupTestFactory.initUserGroup(group = group1)
        val pageable = PageRequest.of(0, 10)
        val pageableCaptor = ArgumentCaptor.forClass(Pageable::class.java)

        Mockito
            .`when`(userGroupRepository!!.findByUserId(eqArg(userId), anyPageable()))
            .thenReturn(PageImpl(listOf(userGroup), pageable, 1))

        val result = groupService!!.getUserGroups(userId, pageable)

        Mockito.verify(userGroupRepository!!).findByUserId(eqArg(userId), capturePageable(pageableCaptor))
        val captured = pageableCaptor.value
        assertThat(captured.sort.isUnsorted).isTrue()
        assertThat(captured.sort.getOrderFor("group.name")).isNull()
        assertThat(captured.pageNumber).isEqualTo(0)
        assertThat(captured.pageSize).isEqualTo(10)
        assertThat(result.content).containsExactly(group1)
    }
}

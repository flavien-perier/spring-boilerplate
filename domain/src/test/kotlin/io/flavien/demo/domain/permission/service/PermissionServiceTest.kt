package io.flavien.demo.domain.permission.service

import io.flavien.demo.domain.group.GroupTestFactory
import io.flavien.demo.domain.group.entity.UserGroup
import io.flavien.demo.domain.group.repository.GroupRepository
import io.flavien.demo.domain.group.repository.UserGroupRepository
import io.flavien.demo.domain.permission.PermissionTestFactory
import io.flavien.demo.domain.permission.exception.BadPermissionException
import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.permission.repository.GroupPermissionRepository
import io.flavien.demo.domain.permission.repository.UserPermissionRepository
import io.flavien.demo.domain.user.UserTestFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class PermissionServiceTest {
    @InjectMocks
    var service: PermissionService? = null

    @Mock
    var userPermissionRepository: UserPermissionRepository? = null

    @Mock
    var userGroupRepository: UserGroupRepository? = null

    @Mock
    var groupPermissionRepository: GroupPermissionRepository? = null

    @Mock
    var groupRepository: GroupRepository? = null

    private val userId = 1L

    @Test
    fun `Should return an empty set when the user has no permission`() {
        // Given
        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(emptyList())
        Mockito
            .`when`(userGroupRepository!!.findByUserId(userId))
            .thenReturn(emptyList())

        // When
        val result = service!!.getGrantedPermissions(userId)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `Should grant a directly allowed permission`() {
        // Given
        val user = UserTestFactory.initUser(id = userId)
        val directPermission =
            PermissionTestFactory.initUserPermission(
                user = user,
                permission = PermissionEnum.MANAGE_OWN_ACCOUNT,
                allow = true,
            )

        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(listOf(directPermission))
        Mockito
            .`when`(userGroupRepository!!.findByUserId(userId))
            .thenReturn(emptyList())

        // When
        val result = service!!.getGrantedPermissions(userId)

        // Then
        assertThat(result).containsExactly(PermissionEnum.MANAGE_OWN_ACCOUNT)
    }

    @Test
    fun `Should deny when a direct deny overrides a group allow`() {
        // Given
        val user = UserTestFactory.initUser(id = userId)
        val group = GroupTestFactory.initGroup(id = 1L, name = "G")
        val directDeny =
            PermissionTestFactory.initUserPermission(
                user = user,
                permission = PermissionEnum.MANAGE_ALL_USERS,
                allow = false,
            )
        val groupAllow =
            PermissionTestFactory.initGroupPermission(
                group = group,
                permission = PermissionEnum.MANAGE_ALL_USERS,
                allow = true,
            )

        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(listOf(directDeny))
        Mockito
            .`when`(userGroupRepository!!.findByUserId(userId))
            .thenReturn(listOf(UserGroup(user, group)))
        Mockito
            .`when`(groupRepository!!.findById(1L))
            .thenReturn(Optional.of(group))
        Mockito
            .`when`(groupPermissionRepository!!.findByGroupId(1L))
            .thenReturn(listOf(groupAllow))

        // When
        val result = service!!.getGrantedPermissions(userId)

        // Then
        assertThat(result).doesNotContain(PermissionEnum.MANAGE_ALL_USERS)
    }

    @Test
    fun `Should grant a permission allowed by a single root group`() {
        // Given
        val user = UserTestFactory.initUser(id = userId)
        val group = GroupTestFactory.initGroup(id = 1L, name = "G")
        val groupAllow =
            PermissionTestFactory.initGroupPermission(
                group = group,
                permission = PermissionEnum.MANAGE_OWN_SESSIONS,
                allow = true,
            )

        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(emptyList())
        Mockito
            .`when`(userGroupRepository!!.findByUserId(userId))
            .thenReturn(listOf(UserGroup(user, group)))
        Mockito
            .`when`(groupRepository!!.findById(1L))
            .thenReturn(Optional.of(group))
        Mockito
            .`when`(groupPermissionRepository!!.findByGroupId(1L))
            .thenReturn(listOf(groupAllow))

        // When
        val result = service!!.getGrantedPermissions(userId)

        // Then
        assertThat(result).contains(PermissionEnum.MANAGE_OWN_SESSIONS)
    }

    @Test
    fun `Should let the parent group win over the child`() {
        // Given
        val user = UserTestFactory.initUser(id = userId)
        val parent = GroupTestFactory.initGroup(id = 1L, name = "P", parent = null)
        val child = GroupTestFactory.initGroup(id = 2L, name = "C", parent = parent)
        val parentAllow =
            PermissionTestFactory.initGroupPermission(
                group = parent,
                permission = PermissionEnum.MANAGE_ALL_USERS,
                allow = true,
            )
        val childDeny =
            PermissionTestFactory.initGroupPermission(
                group = child,
                permission = PermissionEnum.MANAGE_ALL_USERS,
                allow = false,
            )

        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(emptyList())
        Mockito
            .`when`(userGroupRepository!!.findByUserId(userId))
            .thenReturn(listOf(UserGroup(user, child)))
        Mockito
            .`when`(groupRepository!!.findById(2L))
            .thenReturn(Optional.of(child))
        Mockito
            .`when`(groupPermissionRepository!!.findByGroupId(1L))
            .thenReturn(listOf(parentAllow))
        Mockito
            .`when`(groupPermissionRepository!!.findByGroupId(2L))
            .thenReturn(listOf(childDeny))

        // When
        val result = service!!.getGrantedPermissions(userId)

        // Then
        assertThat(result).contains(PermissionEnum.MANAGE_ALL_USERS)
    }

    @Test
    fun `Should deny on equal-depth conflict (deny wins)`() {
        // Given
        val user = UserTestFactory.initUser(id = userId)
        val groupA = GroupTestFactory.initGroup(id = 1L, name = "A", parent = null)
        val groupB = GroupTestFactory.initGroup(id = 2L, name = "B", parent = null)
        val allowA =
            PermissionTestFactory.initGroupPermission(
                group = groupA,
                permission = PermissionEnum.MANAGE_OWN_SESSIONS,
                allow = true,
            )
        val denyB =
            PermissionTestFactory.initGroupPermission(
                group = groupB,
                permission = PermissionEnum.MANAGE_OWN_SESSIONS,
                allow = false,
            )

        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(emptyList())
        Mockito
            .`when`(userGroupRepository!!.findByUserId(userId))
            .thenReturn(listOf(UserGroup(user, groupA), UserGroup(user, groupB)))
        Mockito
            .`when`(groupRepository!!.findById(1L))
            .thenReturn(Optional.of(groupA))
        Mockito
            .`when`(groupRepository!!.findById(2L))
            .thenReturn(Optional.of(groupB))
        Mockito
            .`when`(groupPermissionRepository!!.findByGroupId(1L))
            .thenReturn(listOf(allowA))
        Mockito
            .`when`(groupPermissionRepository!!.findByGroupId(2L))
            .thenReturn(listOf(denyB))

        // When
        val result = service!!.getGrantedPermissions(userId)

        // Then
        assertThat(result).doesNotContain(PermissionEnum.MANAGE_OWN_SESSIONS)
    }

    @Test
    fun `Should grant a permission defined only by the child`() {
        // Given
        val user = UserTestFactory.initUser(id = userId)
        val parent = GroupTestFactory.initGroup(id = 1L, name = "P", parent = null)
        val child = GroupTestFactory.initGroup(id = 2L, name = "C", parent = parent)
        val childAllow =
            PermissionTestFactory.initGroupPermission(
                group = child,
                permission = PermissionEnum.MANAGE_OWN_SESSIONS,
                allow = true,
            )

        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(emptyList())
        Mockito
            .`when`(userGroupRepository!!.findByUserId(userId))
            .thenReturn(listOf(UserGroup(user, child)))
        Mockito
            .`when`(groupRepository!!.findById(2L))
            .thenReturn(Optional.of(child))
        Mockito
            .`when`(groupPermissionRepository!!.findByGroupId(1L))
            .thenReturn(emptyList())
        Mockito
            .`when`(groupPermissionRepository!!.findByGroupId(2L))
            .thenReturn(listOf(childAllow))

        // When
        val result = service!!.getGrantedPermissions(userId)

        // Then
        assertThat(result).contains(PermissionEnum.MANAGE_OWN_SESSIONS)
    }

    @Test
    fun `Should not loop forever on a cycle and stay consistent`() {
        // Given
        val user = UserTestFactory.initUser(id = userId)
        val groupB = GroupTestFactory.initGroup(id = 2L, name = "B", parent = null)
        val groupA = GroupTestFactory.initGroup(id = 1L, name = "A", parent = groupB)
        groupB.parent = groupA
        val allowA =
            PermissionTestFactory.initGroupPermission(
                group = groupA,
                permission = PermissionEnum.MANAGE_OWN_SESSIONS,
                allow = true,
            )

        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(emptyList())
        Mockito
            .`when`(userGroupRepository!!.findByUserId(userId))
            .thenReturn(listOf(UserGroup(user, groupA)))
        Mockito
            .`when`(groupRepository!!.findById(1L))
            .thenReturn(Optional.of(groupA))
        Mockito
            .`when`(groupPermissionRepository!!.findByGroupId(1L))
            .thenReturn(listOf(allowA))
        Mockito
            .`when`(groupPermissionRepository!!.findByGroupId(2L))
            .thenReturn(emptyList())

        // When
        val result = service!!.getGrantedPermissions(userId)

        // Then
        assertThat(result).contains(PermissionEnum.MANAGE_OWN_SESSIONS)
    }

    @Test
    fun `Should return true when the permission is granted`() {
        // Given
        val user = UserTestFactory.initUser(id = userId)
        val directPermission =
            PermissionTestFactory.initUserPermission(
                user = user,
                permission = PermissionEnum.MANAGE_OWN_ACCOUNT,
                allow = true,
            )

        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(listOf(directPermission))
        Mockito
            .`when`(userGroupRepository!!.findByUserId(userId))
            .thenReturn(emptyList())

        // When
        val result = service!!.hasPermission(userId, PermissionEnum.MANAGE_OWN_ACCOUNT)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `Should return false when the permission is not granted`() {
        // Given
        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(emptyList())
        Mockito
            .`when`(userGroupRepository!!.findByUserId(userId))
            .thenReturn(emptyList())

        // When
        val result = service!!.hasPermission(userId, PermissionEnum.MANAGE_ALL_USERS)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `Should not throw when the permission is granted`() {
        // Given
        val user = UserTestFactory.initUser(id = userId)
        val directPermission =
            PermissionTestFactory.initUserPermission(
                user = user,
                permission = PermissionEnum.MANAGE_OWN_ACCOUNT,
                allow = true,
            )

        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(listOf(directPermission))
        Mockito
            .`when`(userGroupRepository!!.findByUserId(userId))
            .thenReturn(emptyList())

        // When
        service!!.checkPermission(userId, PermissionEnum.MANAGE_OWN_ACCOUNT)

        // Then
        Mockito.verify(userPermissionRepository!!).findByUserId(userId)
    }

    @Test
    fun `Should throw BadPermissionException when the permission is denied`() {
        // Given
        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(emptyList())
        Mockito
            .`when`(userGroupRepository!!.findByUserId(userId))
            .thenReturn(emptyList())

        // When/Then
        assertThrows(BadPermissionException::class.java) {
            service!!.checkPermission(userId, PermissionEnum.MANAGE_ALL_USERS)
        }

        Mockito.verify(userPermissionRepository!!).findByUserId(userId)
        Mockito.verify(groupPermissionRepository!!, Mockito.never()).findByGroupId(anyLong())
    }
}

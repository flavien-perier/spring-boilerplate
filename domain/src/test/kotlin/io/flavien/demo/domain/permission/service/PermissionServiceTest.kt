package io.flavien.demo.domain.permission.service

import io.flavien.demo.domain.permission.PermissionTestFactory
import io.flavien.demo.domain.permission.entity.RolePermission
import io.flavien.demo.domain.permission.entity.UserPermission
import io.flavien.demo.domain.permission.exception.BadPermissionException
import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.permission.model.id.RolePermissionId
import io.flavien.demo.domain.permission.model.id.UserPermissionId
import io.flavien.demo.domain.permission.repository.RolePermissionRepository
import io.flavien.demo.domain.permission.repository.UserPermissionRepository
import io.flavien.demo.domain.role.RoleTestFactory
import io.flavien.demo.domain.role.entity.UserRole
import io.flavien.demo.domain.role.repository.RoleRepository
import io.flavien.demo.domain.role.repository.UserRoleRepository
import io.flavien.demo.domain.user.UserTestFactory
import io.flavien.demo.domain.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageRequest
import java.util.Optional
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class PermissionServiceTest {
    @InjectMocks
    var service: PermissionService? = null

    @Mock
    var userPermissionRepository: UserPermissionRepository? = null

    @Mock
    var userRoleRepository: UserRoleRepository? = null

    @Mock
    var rolePermissionRepository: RolePermissionRepository? = null

    @Mock
    var roleRepository: RoleRepository? = null

    @Mock
    var userRepository: UserRepository? = null

    private val userId = UUID.fromString("00000000-0000-0000-0000-00000000000a")
    private val role1Id = UUID.fromString("00000000-0000-0000-0000-000000000001")
    private val role2Id = UUID.fromString("00000000-0000-0000-0000-000000000002")

    @Test
    fun `Should return an empty set when the user has no permission`() {
        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(emptyList())
        Mockito
            .`when`(userRoleRepository!!.findByUserId(userId))
            .thenReturn(emptyList())

        val result = service!!.getGrantedPermissions(userId)

        assertThat(result).isEmpty()
    }

    @Test
    fun `Should grant a directly allowed permission`() {
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
            .`when`(userRoleRepository!!.findByUserId(userId))
            .thenReturn(emptyList())

        val result = service!!.getGrantedPermissions(userId)

        assertThat(result).containsExactly(PermissionEnum.MANAGE_OWN_ACCOUNT)
    }

    @Test
    fun `Should let a direct user deny win over a role allow`() {
        val user = UserTestFactory.initUser(id = userId)
        val role = RoleTestFactory.initRole(id = role1Id, name = "G")
        val directDeny =
            PermissionTestFactory.initUserPermission(
                user = user,
                permission = PermissionEnum.MANAGE_ALL_USERS,
                allow = false,
            )
        val roleAllow =
            PermissionTestFactory.initRolePermission(
                role = role,
                permission = PermissionEnum.MANAGE_ALL_USERS,
                allow = true,
            )

        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(listOf(directDeny))
        Mockito
            .`when`(userRoleRepository!!.findByUserId(userId))
            .thenReturn(listOf(UserRole(user, role)))
        Mockito
            .`when`(roleRepository!!.findById(role1Id))
            .thenReturn(Optional.of(role))
        Mockito
            .`when`(rolePermissionRepository!!.findByRoleId(role1Id))
            .thenReturn(listOf(roleAllow))

        val result = service!!.getGrantedPermissions(userId)

        assertThat(result).doesNotContain(PermissionEnum.MANAGE_ALL_USERS)
    }

    @Test
    fun `Should let a direct user allow win over a role deny`() {
        val user = UserTestFactory.initUser(id = userId)
        val role = RoleTestFactory.initRole(id = role1Id, name = "G")
        val userAllow =
            PermissionTestFactory.initUserPermission(
                user = user,
                permission = PermissionEnum.MANAGE_ALL_USERS,
                allow = true,
            )
        val roleDeny =
            PermissionTestFactory.initRolePermission(
                role = role,
                permission = PermissionEnum.MANAGE_ALL_USERS,
                allow = false,
            )

        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(listOf(userAllow))
        Mockito
            .`when`(userRoleRepository!!.findByUserId(userId))
            .thenReturn(listOf(UserRole(user, role)))
        Mockito
            .`when`(roleRepository!!.findById(role1Id))
            .thenReturn(Optional.of(role))
        Mockito
            .`when`(rolePermissionRepository!!.findByRoleId(role1Id))
            .thenReturn(listOf(roleDeny))

        val result = service!!.getGrantedPermissions(userId)

        assertThat(result).contains(PermissionEnum.MANAGE_ALL_USERS)
    }

    @Test
    fun `Should apply a user override when no role defines the permission`() {
        val user = UserTestFactory.initUser(id = userId)
        val role = RoleTestFactory.initRole(id = role1Id, name = "G")
        val userAllow =
            PermissionTestFactory.initUserPermission(
                user = user,
                permission = PermissionEnum.MANAGE_OWN_ACCOUNT,
                allow = true,
            )

        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(listOf(userAllow))
        Mockito
            .`when`(userRoleRepository!!.findByUserId(userId))
            .thenReturn(listOf(UserRole(user, role)))
        Mockito
            .`when`(roleRepository!!.findById(role1Id))
            .thenReturn(Optional.of(role))
        Mockito
            .`when`(rolePermissionRepository!!.findByRoleId(role1Id))
            .thenReturn(emptyList())

        val result = service!!.getGrantedPermissions(userId)

        assertThat(result).containsExactly(PermissionEnum.MANAGE_OWN_ACCOUNT)
    }

    @Test
    fun `Should grant a permission allowed by a single root role`() {
        val user = UserTestFactory.initUser(id = userId)
        val role = RoleTestFactory.initRole(id = role1Id, name = "G")
        val roleAllow =
            PermissionTestFactory.initRolePermission(
                role = role,
                permission = PermissionEnum.MANAGE_OWN_SESSIONS,
                allow = true,
            )

        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(emptyList())
        Mockito
            .`when`(userRoleRepository!!.findByUserId(userId))
            .thenReturn(listOf(UserRole(user, role)))
        Mockito
            .`when`(roleRepository!!.findById(role1Id))
            .thenReturn(Optional.of(role))
        Mockito
            .`when`(rolePermissionRepository!!.findByRoleId(role1Id))
            .thenReturn(listOf(roleAllow))

        val result = service!!.getGrantedPermissions(userId)

        assertThat(result).contains(PermissionEnum.MANAGE_OWN_SESSIONS)
    }

    @Test
    fun `Should grant when an ancestor allows even if the child denies (allow wins)`() {
        val user = UserTestFactory.initUser(id = userId)
        val parent = RoleTestFactory.initRole(id = role1Id, name = "P", parent = null)
        val child = RoleTestFactory.initRole(id = role2Id, name = "C", parent = parent)
        val parentAllow =
            PermissionTestFactory.initRolePermission(
                role = parent,
                permission = PermissionEnum.MANAGE_ALL_USERS,
                allow = true,
            )
        val childDeny =
            PermissionTestFactory.initRolePermission(
                role = child,
                permission = PermissionEnum.MANAGE_ALL_USERS,
                allow = false,
            )

        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(emptyList())
        Mockito
            .`when`(userRoleRepository!!.findByUserId(userId))
            .thenReturn(listOf(UserRole(user, child)))
        Mockito
            .`when`(roleRepository!!.findById(role2Id))
            .thenReturn(Optional.of(child))
        Mockito
            .`when`(rolePermissionRepository!!.findByRoleId(role1Id))
            .thenReturn(listOf(parentAllow))
        Mockito
            .`when`(rolePermissionRepository!!.findByRoleId(role2Id))
            .thenReturn(listOf(childDeny))

        val result = service!!.getGrantedPermissions(userId)

        assertThat(result).contains(PermissionEnum.MANAGE_ALL_USERS)
    }

    @Test
    fun `Should grant when any role allows even if another role denies (allow wins)`() {
        val user = UserTestFactory.initUser(id = userId)
        val roleA = RoleTestFactory.initRole(id = role1Id, name = "A", parent = null)
        val roleB = RoleTestFactory.initRole(id = role2Id, name = "B", parent = null)
        val allowA =
            PermissionTestFactory.initRolePermission(
                role = roleA,
                permission = PermissionEnum.MANAGE_OWN_SESSIONS,
                allow = true,
            )
        val denyB =
            PermissionTestFactory.initRolePermission(
                role = roleB,
                permission = PermissionEnum.MANAGE_OWN_SESSIONS,
                allow = false,
            )

        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(emptyList())
        Mockito
            .`when`(userRoleRepository!!.findByUserId(userId))
            .thenReturn(listOf(UserRole(user, roleA), UserRole(user, roleB)))
        Mockito
            .`when`(roleRepository!!.findById(role1Id))
            .thenReturn(Optional.of(roleA))
        Mockito
            .`when`(roleRepository!!.findById(role2Id))
            .thenReturn(Optional.of(roleB))
        Mockito
            .`when`(rolePermissionRepository!!.findByRoleId(role1Id))
            .thenReturn(listOf(allowA))
        Mockito
            .`when`(rolePermissionRepository!!.findByRoleId(role2Id))
            .thenReturn(listOf(denyB))

        val result = service!!.getGrantedPermissions(userId)

        assertThat(result).contains(PermissionEnum.MANAGE_OWN_SESSIONS)
    }

    @Test
    fun `Should grant a permission defined only by the child`() {
        val user = UserTestFactory.initUser(id = userId)
        val parent = RoleTestFactory.initRole(id = role1Id, name = "P", parent = null)
        val child = RoleTestFactory.initRole(id = role2Id, name = "C", parent = parent)
        val childAllow =
            PermissionTestFactory.initRolePermission(
                role = child,
                permission = PermissionEnum.MANAGE_OWN_SESSIONS,
                allow = true,
            )

        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(emptyList())
        Mockito
            .`when`(userRoleRepository!!.findByUserId(userId))
            .thenReturn(listOf(UserRole(user, child)))
        Mockito
            .`when`(roleRepository!!.findById(role2Id))
            .thenReturn(Optional.of(child))
        Mockito
            .`when`(rolePermissionRepository!!.findByRoleId(role1Id))
            .thenReturn(emptyList())
        Mockito
            .`when`(rolePermissionRepository!!.findByRoleId(role2Id))
            .thenReturn(listOf(childAllow))

        val result = service!!.getGrantedPermissions(userId)

        assertThat(result).contains(PermissionEnum.MANAGE_OWN_SESSIONS)
    }

    @Test
    fun `Should not loop forever on a cycle and stay consistent`() {
        val user = UserTestFactory.initUser(id = userId)
        val roleB = RoleTestFactory.initRole(id = role2Id, name = "B", parent = null)
        val roleA = RoleTestFactory.initRole(id = role1Id, name = "A", parent = roleB)
        roleB.parent = roleA
        val allowA =
            PermissionTestFactory.initRolePermission(
                role = roleA,
                permission = PermissionEnum.MANAGE_OWN_SESSIONS,
                allow = true,
            )

        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(emptyList())
        Mockito
            .`when`(userRoleRepository!!.findByUserId(userId))
            .thenReturn(listOf(UserRole(user, roleA)))
        Mockito
            .`when`(roleRepository!!.findById(role1Id))
            .thenReturn(Optional.of(roleA))
        Mockito
            .`when`(rolePermissionRepository!!.findByRoleId(role1Id))
            .thenReturn(listOf(allowA))
        Mockito
            .`when`(rolePermissionRepository!!.findByRoleId(role2Id))
            .thenReturn(emptyList())

        val result = service!!.getGrantedPermissions(userId)

        assertThat(result).contains(PermissionEnum.MANAGE_OWN_SESSIONS)
    }

    @Test
    fun `Should grant when one of several roles allows even if another denies`() {
        val user = UserTestFactory.initUser(id = userId)
        val roleAllowing = RoleTestFactory.initRole(id = role1Id, name = "Allowing", parent = null)
        val roleDenying = RoleTestFactory.initRole(id = role2Id, name = "Denying", parent = null)
        val allow =
            PermissionTestFactory.initRolePermission(
                role = roleAllowing,
                permission = PermissionEnum.MANAGE_ALL_USERS,
                allow = true,
            )
        val deny =
            PermissionTestFactory.initRolePermission(
                role = roleDenying,
                permission = PermissionEnum.MANAGE_ALL_USERS,
                allow = false,
            )

        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(emptyList())
        Mockito
            .`when`(userRoleRepository!!.findByUserId(userId))
            .thenReturn(listOf(UserRole(user, roleAllowing), UserRole(user, roleDenying)))
        Mockito
            .`when`(roleRepository!!.findById(role1Id))
            .thenReturn(Optional.of(roleAllowing))
        Mockito
            .`when`(roleRepository!!.findById(role2Id))
            .thenReturn(Optional.of(roleDenying))
        Mockito
            .`when`(rolePermissionRepository!!.findByRoleId(role1Id))
            .thenReturn(listOf(allow))
        Mockito
            .`when`(rolePermissionRepository!!.findByRoleId(role2Id))
            .thenReturn(listOf(deny))

        val result = service!!.getGrantedPermissions(userId)

        assertThat(result).contains(PermissionEnum.MANAGE_ALL_USERS)
    }

    @Test
    fun `Should return true when the permission is granted`() {
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
            .`when`(userRoleRepository!!.findByUserId(userId))
            .thenReturn(emptyList())

        val result = service!!.hasPermission(userId, PermissionEnum.MANAGE_OWN_ACCOUNT)

        assertThat(result).isTrue()
    }

    @Test
    fun `Should return false when the permission is not granted`() {
        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(emptyList())
        Mockito
            .`when`(userRoleRepository!!.findByUserId(userId))
            .thenReturn(emptyList())

        val result = service!!.hasPermission(userId, PermissionEnum.MANAGE_ALL_USERS)

        assertThat(result).isFalse()
    }

    @Test
    fun `Should not throw when the permission is granted`() {
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
            .`when`(userRoleRepository!!.findByUserId(userId))
            .thenReturn(emptyList())

        service!!.checkPermission(userId, PermissionEnum.MANAGE_OWN_ACCOUNT)

        Mockito.verify(userPermissionRepository!!).findByUserId(userId)
    }

    @Test
    fun `Should throw BadPermissionException when the permission is denied`() {
        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(emptyList())
        Mockito
            .`when`(userRoleRepository!!.findByUserId(userId))
            .thenReturn(emptyList())

        assertThrows(BadPermissionException::class.java) {
            service!!.checkPermission(userId, PermissionEnum.MANAGE_ALL_USERS)
        }

        Mockito.verify(userPermissionRepository!!).findByUserId(userId)
    }

    @Test
    fun `Should return full permission catalog for a role with mixed overrides`() {
        val define =
            PermissionTestFactory.initRolePermission(
                role = RoleTestFactory.initRole(id = role1Id),
                permission = PermissionEnum.MANAGE_ALL_USERS,
                allow = true,
            )

        Mockito.`when`(rolePermissionRepository!!.findByRoleId(role1Id)).thenReturn(listOf(define))

        val result = service!!.getRolePermissions(role1Id)

        assertThat(result).hasSize(PermissionEnum.entries.size)
        val defined = result.first { it.permission == PermissionEnum.MANAGE_ALL_USERS }
        assertThat(defined.allow).isTrue()
        val inherited = result.first { it.permission == PermissionEnum.MANAGE_OWN_ACCOUNT }
        assertThat(inherited.allow).isNull()
    }

    @Test
    fun `Should return full permission catalog for user overrides`() {
        val define =
            PermissionTestFactory.initUserPermission(
                user = UserTestFactory.initUser(id = userId),
                permission = PermissionEnum.MANAGE_OWN_SESSIONS,
                allow = false,
            )

        Mockito.`when`(userPermissionRepository!!.findByUserId(userId)).thenReturn(listOf(define))

        val result = service!!.getUserPermissionOverrides(userId, PageRequest.of(0, 10))

        assertThat(result.content).hasSize(PermissionEnum.entries.size)
        val denied = result.content.first { it.permission == PermissionEnum.MANAGE_OWN_SESSIONS }
        assertThat(denied.allow).isFalse()
        val inherited = result.content.first { it.permission == PermissionEnum.MANAGE_ALL_USERS }
        assertThat(inherited.allow).isNull()
        assertThat(result.totalElements).isEqualTo(PermissionEnum.entries.size.toLong())
    }

    @Test
    fun `Should expose the inherited value of user permissions granted by a role without locking them`() {
        val user = UserTestFactory.initUser(id = userId)
        val role = RoleTestFactory.initRole(id = role1Id, name = "G")
        val roleAllow =
            PermissionTestFactory.initRolePermission(
                role = role,
                permission = PermissionEnum.MANAGE_OWN_ACCOUNT,
                allow = true,
            )

        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(emptyList())
        Mockito
            .`when`(userRoleRepository!!.findByUserId(userId))
            .thenReturn(listOf(UserRole(user, role)))
        Mockito
            .`when`(roleRepository!!.findById(role1Id))
            .thenReturn(Optional.of(role))
        Mockito
            .`when`(rolePermissionRepository!!.findByRoleId(role1Id))
            .thenReturn(listOf(roleAllow))

        val result = service!!.getUserPermissionOverrides(userId, PageRequest.of(0, 10))

        val inheritedRow = result.content.first { it.permission == PermissionEnum.MANAGE_OWN_ACCOUNT }
        assertThat(inheritedRow.locked).isFalse()
        assertThat(inheritedRow.inheritedAllow).isTrue()
        val free = result.content.first { it.permission == PermissionEnum.MANAGE_ALL_USERS }
        assertThat(free.locked).isFalse()
        assertThat(free.inheritedAllow).isNull()
    }

    @Test
    fun `Should flag role permissions locked by an ancestor with the inherited value`() {
        val parent = RoleTestFactory.initRole(id = role1Id, name = "P", parent = null)
        val child = RoleTestFactory.initRole(id = role2Id, name = "C", parent = parent)
        val parentAllow =
            PermissionTestFactory.initRolePermission(
                role = parent,
                permission = PermissionEnum.MANAGE_OWN_ACCOUNT,
                allow = true,
            )

        Mockito
            .`when`(rolePermissionRepository!!.findByRoleId(role2Id))
            .thenReturn(emptyList())
        Mockito
            .`when`(roleRepository!!.findById(role2Id))
            .thenReturn(Optional.of(child))
        Mockito
            .`when`(roleRepository!!.findById(role1Id))
            .thenReturn(Optional.of(parent))
        Mockito
            .`when`(rolePermissionRepository!!.findByRoleId(role1Id))
            .thenReturn(listOf(parentAllow))

        val result = service!!.getRolePermissions(role2Id)

        val locked = result.first { it.permission == PermissionEnum.MANAGE_OWN_ACCOUNT }
        assertThat(locked.locked).isTrue()
        assertThat(locked.inheritedAllow).isTrue()
        assertThat(locked.allow).isNull()
    }

    @Test
    fun `Should not flag a role own permission as locked`() {
        val role = RoleTestFactory.initRole(id = role1Id, name = "G", parent = null)
        val own =
            PermissionTestFactory.initRolePermission(
                role = role,
                permission = PermissionEnum.MANAGE_ALL_USERS,
                allow = true,
            )

        Mockito
            .`when`(rolePermissionRepository!!.findByRoleId(role1Id))
            .thenReturn(listOf(own))
        Mockito
            .`when`(roleRepository!!.findById(role1Id))
            .thenReturn(Optional.of(role))

        val result = service!!.getRolePermissions(role1Id)

        val setting = result.first { it.permission == PermissionEnum.MANAGE_ALL_USERS }
        assertThat(setting.locked).isFalse()
        assertThat(setting.allow).isTrue()
    }

    @Test
    fun `Should mark a user override granted by a role as not locked with inherited allow true`() {
        val user = UserTestFactory.initUser(id = userId)
        val role = RoleTestFactory.initRole(id = role1Id, name = "G")
        val roleAllow =
            PermissionTestFactory.initRolePermission(
                role = role,
                permission = PermissionEnum.MANAGE_ALL_USERS,
                allow = true,
            )

        Mockito
            .`when`(userPermissionRepository!!.findByUserId(userId))
            .thenReturn(emptyList())
        Mockito
            .`when`(userRoleRepository!!.findByUserId(userId))
            .thenReturn(listOf(UserRole(user, role)))
        Mockito
            .`when`(roleRepository!!.findById(role1Id))
            .thenReturn(Optional.of(role))
        Mockito
            .`when`(rolePermissionRepository!!.findByRoleId(role1Id))
            .thenReturn(listOf(roleAllow))

        val result = service!!.getUserPermissionOverrides(userId, PageRequest.of(0, 10))

        val granted = result.content.first { it.permission == PermissionEnum.MANAGE_ALL_USERS }
        assertThat(granted.locked).isFalse()
        assertThat(granted.inheritedAllow).isTrue()
    }

    @Test
    fun `Should not lock a role permission when a strict ancestor denies it`() {
        val parent = RoleTestFactory.initRole(id = role1Id, name = "P", parent = null)
        val child = RoleTestFactory.initRole(id = role2Id, name = "C", parent = parent)
        val parentDeny =
            PermissionTestFactory.initRolePermission(
                role = parent,
                permission = PermissionEnum.MANAGE_OWN_ACCOUNT,
                allow = false,
            )

        Mockito
            .`when`(rolePermissionRepository!!.findByRoleId(role2Id))
            .thenReturn(emptyList())
        Mockito
            .`when`(roleRepository!!.findById(role2Id))
            .thenReturn(Optional.of(child))
        Mockito
            .`when`(roleRepository!!.findById(role1Id))
            .thenReturn(Optional.of(parent))
        Mockito
            .`when`(rolePermissionRepository!!.findByRoleId(role1Id))
            .thenReturn(listOf(parentDeny))

        val result = service!!.getRolePermissions(role2Id)

        val setting = result.first { it.permission == PermissionEnum.MANAGE_OWN_ACCOUNT }
        assertThat(setting.locked).isFalse()
        assertThat(setting.inheritedAllow).isNull()
    }

    @Test
    fun `Should set a role permission`() {
        val role = RoleTestFactory.initRole(id = role1Id, name = "G")

        Mockito.`when`(roleRepository!!.findById(role1Id)).thenReturn(Optional.of(role))

        service!!.setRolePermission(role1Id, PermissionEnum.MANAGE_ALL_ROLES, true)

        Mockito.verify(rolePermissionRepository!!).save(RolePermission(role, PermissionEnum.MANAGE_ALL_ROLES, true))
    }

    @Test
    fun `Should remove a role permission`() {
        service!!.removeRolePermission(role1Id, PermissionEnum.MANAGE_ALL_ROLES)

        Mockito.verify(rolePermissionRepository!!).deleteById(RolePermissionId(role1Id, PermissionEnum.MANAGE_ALL_ROLES))
    }

    @Test
    fun `Should set a user permission`() {
        val user = UserTestFactory.initUser(id = userId)

        Mockito.`when`(userRepository!!.getUserById(userId)).thenReturn(Optional.of(user))

        service!!.setUserPermission(userId, PermissionEnum.MANAGE_ALL_USERS, false)

        Mockito.verify(userPermissionRepository!!).save(UserPermission(user, PermissionEnum.MANAGE_ALL_USERS, false))
    }

    @Test
    fun `Should remove a user permission`() {
        service!!.removeUserPermission(userId, PermissionEnum.MANAGE_ALL_USERS)

        Mockito.verify(userPermissionRepository!!).deleteById(UserPermissionId(userId, PermissionEnum.MANAGE_ALL_USERS))
    }
}

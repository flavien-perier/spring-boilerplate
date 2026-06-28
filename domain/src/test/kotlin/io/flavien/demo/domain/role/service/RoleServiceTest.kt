package io.flavien.demo.domain.role.service

import io.flavien.demo.domain.permission.repository.RolePermissionRepository
import io.flavien.demo.domain.role.RoleTestFactory
import io.flavien.demo.domain.role.entity.UserRole
import io.flavien.demo.domain.role.exception.ProtectedRoleException
import io.flavien.demo.domain.role.exception.RoleAlreadyExistsException
import io.flavien.demo.domain.role.exception.RoleHierarchyException
import io.flavien.demo.domain.role.exception.RoleNotFoundException
import io.flavien.demo.domain.role.model.id.UserRoleId
import io.flavien.demo.domain.role.repository.RoleRepository
import io.flavien.demo.domain.role.repository.UserRoleRepository
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
class RoleServiceTest {
    @InjectMocks
    var roleService: RoleService? = null

    @Mock
    var roleRepository: RoleRepository? = null

    @Mock
    var userRoleRepository: UserRoleRepository? = null

    @Mock
    var rolePermissionRepository: RolePermissionRepository? = null

    @Mock
    var userRepository: UserRepository? = null

    private val userId = UUID.fromString("00000000-0000-0000-0000-00000000000a")
    private val role1Id = UUID.fromString("00000000-0000-0000-0000-000000000001")
    private val role2Id = UUID.fromString("00000000-0000-0000-0000-000000000002")
    private val role10Id = UUID.fromString("00000000-0000-0000-0000-000000000010")

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
    fun `Should assign the default role when the user has none`() {
        val user = UserTestFactory.initUser(id = userId)
        val role = RoleTestFactory.initRole(id = role10Id, name = "USER")

        Mockito
            .`when`(roleRepository!!.findByName("USER"))
            .thenReturn(Optional.of(role))
        Mockito
            .`when`(userRoleRepository!!.existsById(UserRoleId(user.id!!, role.id!!)))
            .thenReturn(false)

        roleService!!.assignDefaultRole(user)

        Mockito.verify(roleRepository!!).findByName("USER")
        Mockito.verify(userRoleRepository!!).existsById(UserRoleId(user.id!!, role.id!!))
        Mockito.verify(userRoleRepository!!).save(UserRole(user, role))
    }

    @Test
    fun `Should not re-assign the default role when already present`() {
        val user = UserTestFactory.initUser(id = userId)
        val role = RoleTestFactory.initRole(id = role10Id, name = "USER")

        Mockito
            .`when`(roleRepository!!.findByName("USER"))
            .thenReturn(Optional.of(role))
        Mockito
            .`when`(userRoleRepository!!.existsById(UserRoleId(user.id!!, role.id!!)))
            .thenReturn(true)

        roleService!!.assignDefaultRole(user)

        Mockito.verify(roleRepository!!).findByName("USER")
        Mockito.verify(userRoleRepository!!).existsById(UserRoleId(user.id!!, role.id!!))
        Mockito.verify(userRoleRepository!!, Mockito.never()).save(any())
    }

    @Test
    fun `Should fail when the default role is missing`() {
        val user = UserTestFactory.initUser(id = userId)

        Mockito
            .`when`(roleRepository!!.findByName("USER"))
            .thenReturn(Optional.empty())

        assertThrows(IllegalStateException::class.java) {
            roleService!!.assignDefaultRole(user)
        }

        Mockito.verify(roleRepository!!).findByName("USER")
        Mockito.verify(userRoleRepository!!, Mockito.never()).save(any())
    }

    @Test
    fun `Should find all roles`() {
        val g1 = RoleTestFactory.initRole(id = role1Id, name = "G1")
        val g2 = RoleTestFactory.initRole(id = role2Id, name = "G2")
        val pageable = PageRequest.of(0, 10)

        Mockito.`when`(roleRepository!!.findAll(pageable)).thenReturn(PageImpl(listOf(g1, g2), pageable, 2))

        val result = roleService!!.findAll(pageable)

        assertThat(result.content).containsExactly(g1, g2)
        assertThat(result.totalElements).isEqualTo(2)
        assertThat(result.totalPages).isEqualTo(1)
    }

    @Test
    fun `Should get role by id`() {
        val role = RoleTestFactory.initRole(id = role1Id, name = "G")

        Mockito.`when`(roleRepository!!.findById(role1Id)).thenReturn(Optional.of(role))

        val result = roleService!!.getById(role1Id)

        assertThat(result).isEqualTo(role)
    }

    @Test
    fun `Should throw RoleNotFoundException when role not found`() {
        Mockito.`when`(roleRepository!!.findById(role1Id)).thenReturn(Optional.empty())

        assertThrows(RoleNotFoundException::class.java) {
            roleService!!.getById(role1Id)
        }
    }

    @Test
    fun `Should create a role`() {
        val saved = RoleTestFactory.initRole(id = role1Id, name = "NEW")
        val parent = RoleTestFactory.initRole(id = role10Id, name = "PARENT")

        Mockito.`when`(roleRepository!!.existsByName("NEW")).thenReturn(false)
        Mockito.`when`(roleRepository!!.findById(role10Id)).thenReturn(Optional.of(parent))
        Mockito.`when`(roleRepository!!.save(any())).thenReturn(saved)

        val result = roleService!!.create("NEW", role10Id)

        assertThat(result.id).isEqualTo(role1Id)
        assertThat(result.name).isEqualTo("NEW")
        Mockito.verify(roleRepository!!).existsByName("NEW")
    }

    @Test
    fun `Should fail to create a role with duplicate name`() {
        Mockito.`when`(roleRepository!!.existsByName("DUP")).thenReturn(true)

        assertThrows(RoleAlreadyExistsException::class.java) {
            roleService!!.create("DUP", null)
        }
    }

    @Test
    fun `Should update a role name`() {
        val existing = RoleTestFactory.initRole(id = role1Id, name = "OLD")
        val updated = RoleTestFactory.initRole(id = role1Id, name = "NEW")

        Mockito.`when`(roleRepository!!.findById(role1Id)).thenReturn(Optional.of(existing))
        Mockito.`when`(roleRepository!!.existsByName("NEW")).thenReturn(false)
        Mockito.`when`(roleRepository!!.save(any())).thenReturn(updated)

        val result = roleService!!.update(role1Id, "NEW", null)

        assertThat(result.name).isEqualTo("NEW")
    }

    @Test
    fun `Should remove the parent when parentId is null`() {
        val parent = RoleTestFactory.initRole(id = role10Id, name = "PARENT")
        val existing = RoleTestFactory.initRole(id = role1Id, name = "CHILD", parent = parent)

        Mockito.`when`(roleRepository!!.findById(role1Id)).thenReturn(Optional.of(existing))
        Mockito.`when`(roleRepository!!.save(any())).thenReturn(existing)

        roleService!!.update(role1Id, null, null)

        assertThat(existing.parent).isNull()
    }

    @Test
    fun `Should fail to update with duplicate name`() {
        val existing = RoleTestFactory.initRole(id = role1Id, name = "OLD")

        Mockito.`when`(roleRepository!!.findById(role1Id)).thenReturn(Optional.of(existing))
        Mockito.`when`(roleRepository!!.existsByName("DUP")).thenReturn(true)

        assertThrows(RoleAlreadyExistsException::class.java) {
            roleService!!.update(role1Id, "DUP", null)
        }
    }

    @Test
    fun `Should reject setting parentId to itself`() {
        val role = RoleTestFactory.initRole(id = role1Id, name = "G")

        Mockito.`when`(roleRepository!!.findById(role1Id)).thenReturn(Optional.of(role))

        assertThrows(RoleHierarchyException::class.java) {
            roleService!!.update(role1Id, null, role1Id)
        }
    }

    @Test
    fun `Should reject setting parentId to a descendant`() {
        val grandchild = RoleTestFactory.initRole(id = role1Id, name = "GC")
        val child = RoleTestFactory.initRole(id = role2Id, name = "C", parent = grandchild)

        Mockito.`when`(roleRepository!!.findById(role1Id)).thenReturn(Optional.of(grandchild))
        Mockito.`when`(roleRepository!!.findById(role2Id)).thenReturn(Optional.of(child))

        assertThrows(RoleHierarchyException::class.java) {
            roleService!!.update(role1Id, null, role2Id)
        }
    }

    @Test
    fun `Should delete a role`() {
        val role = RoleTestFactory.initRole(id = role1Id, name = "CUSTOM")

        Mockito.`when`(roleRepository!!.findById(role1Id)).thenReturn(Optional.of(role))
        Mockito.`when`(roleRepository!!.existsByParentId(role1Id)).thenReturn(false)

        roleService!!.delete(role1Id)

        Mockito.verify(rolePermissionRepository!!).deleteByRoleId(role1Id)
        Mockito.verify(userRoleRepository!!).deleteByRoleId(role1Id)
        Mockito.verify(roleRepository!!).deleteById(role1Id)
    }

    @Test
    fun `Should reject delete of protected USER role`() {
        val role = RoleTestFactory.initRole(id = role1Id, name = "USER")

        Mockito.`when`(roleRepository!!.findById(role1Id)).thenReturn(Optional.of(role))

        assertThrows(ProtectedRoleException::class.java) {
            roleService!!.delete(role1Id)
        }
    }

    @Test
    fun `Should reject delete of protected ADMIN role`() {
        val role = RoleTestFactory.initRole(id = role1Id, name = "ADMIN")

        Mockito.`when`(roleRepository!!.findById(role1Id)).thenReturn(Optional.of(role))

        assertThrows(ProtectedRoleException::class.java) {
            roleService!!.delete(role1Id)
        }
    }

    @Test
    fun `Should reject delete of role with children`() {
        val role = RoleTestFactory.initRole(id = role1Id, name = "PARENT")

        Mockito.`when`(roleRepository!!.findById(role1Id)).thenReturn(Optional.of(role))
        Mockito.`when`(roleRepository!!.existsByParentId(role1Id)).thenReturn(true)

        assertThrows(RoleHierarchyException::class.java) {
            roleService!!.delete(role1Id)
        }
    }

    @Test
    fun `Should add user to role`() {
        val user = UserTestFactory.initUser(id = userId)
        val role = RoleTestFactory.initRole(id = role10Id, name = "G")

        Mockito.`when`(userRepository!!.getUserById(userId)).thenReturn(Optional.of(user))
        Mockito.`when`(roleRepository!!.findById(role10Id)).thenReturn(Optional.of(role))
        Mockito.`when`(userRoleRepository!!.existsById(UserRoleId(userId, role10Id))).thenReturn(false)

        roleService!!.addUserToRole(userId, role10Id)

        Mockito.verify(userRoleRepository!!).save(UserRole(user, role))
    }

    @Test
    fun `Should not duplicate user role membership`() {
        val user = UserTestFactory.initUser(id = userId)
        val role = RoleTestFactory.initRole(id = role10Id, name = "G")

        Mockito.`when`(userRepository!!.getUserById(userId)).thenReturn(Optional.of(user))
        Mockito.`when`(roleRepository!!.findById(role10Id)).thenReturn(Optional.of(role))
        Mockito.`when`(userRoleRepository!!.existsById(UserRoleId(userId, role10Id))).thenReturn(true)

        roleService!!.addUserToRole(userId, role10Id)

        Mockito.verify(userRoleRepository!!, Mockito.never()).save(any())
    }

    @Test
    fun `Should remove user from role`() {
        roleService!!.removeUserFromRole(userId, role10Id)

        Mockito.verify(userRoleRepository!!).deleteById(UserRoleId(userId, role10Id))
    }

    @Test
    fun `Should get user roles`() {
        val role1 = RoleTestFactory.initRole(id = role1Id, name = "G1")
        val userRole = RoleTestFactory.initUserRole(role = role1)
        val pageable = PageRequest.of(0, 10)

        Mockito
            .`when`(userRoleRepository!!.findByUserId(userId, pageable))
            .thenReturn(PageImpl(listOf(userRole), pageable, 1))

        val result = roleService!!.getUserRoles(userId, pageable)

        assertThat(result.content).containsExactly(role1)
        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.totalPages).isEqualTo(1)
    }

    @Test
    fun `Should remap sort by name ascending onto the role association when getting user roles`() {
        val role1 = RoleTestFactory.initRole(id = role1Id, name = "G1")
        val userRole = RoleTestFactory.initUserRole(role = role1)
        val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"))
        val pageableCaptor = ArgumentCaptor.forClass(Pageable::class.java)

        Mockito
            .`when`(userRoleRepository!!.findByUserId(eqArg(userId), anyPageable()))
            .thenReturn(PageImpl(listOf(userRole), PageRequest.of(0, 10), 1))

        val result = roleService!!.getUserRoles(userId, pageable)

        Mockito.verify(userRoleRepository!!).findByUserId(eqArg(userId), capturePageable(pageableCaptor))
        val captured = pageableCaptor.value
        val order = captured.sort.getOrderFor("role.name")
        assertThat(order).isNotNull()
        assertThat(order!!.direction).isEqualTo(Sort.Direction.ASC)
        assertThat(captured.sort.getOrderFor("name")).isNull()
        assertThat(captured.pageNumber).isEqualTo(0)
        assertThat(captured.pageSize).isEqualTo(10)
        assertThat(result.content).containsExactly(role1)
    }

    @Test
    fun `Should remap sort by name descending onto the role association when getting user roles`() {
        val role1 = RoleTestFactory.initRole(id = role1Id, name = "G1")
        val role2 = RoleTestFactory.initRole(id = role2Id, name = "G2")
        val userRole1 = RoleTestFactory.initUserRole(role = role1)
        val userRole2 = RoleTestFactory.initUserRole(role = role2)
        val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name"))
        val pageableCaptor = ArgumentCaptor.forClass(Pageable::class.java)

        Mockito
            .`when`(userRoleRepository!!.findByUserId(eqArg(userId), anyPageable()))
            .thenReturn(PageImpl(listOf(userRole2, userRole1), PageRequest.of(0, 10), 2))

        val result = roleService!!.getUserRoles(userId, pageable)

        Mockito.verify(userRoleRepository!!).findByUserId(eqArg(userId), capturePageable(pageableCaptor))
        val captured = pageableCaptor.value
        val order = captured.sort.getOrderFor("role.name")
        assertThat(order).isNotNull()
        assertThat(order!!.direction).isEqualTo(Sort.Direction.DESC)
        assertThat(captured.sort.getOrderFor("name")).isNull()
        assertThat(result.content).containsExactly(role2, role1)
    }

    @Test
    fun `Should leave the pageable unsorted when getting user roles without a sort`() {
        val role1 = RoleTestFactory.initRole(id = role1Id, name = "G1")
        val userRole = RoleTestFactory.initUserRole(role = role1)
        val pageable = PageRequest.of(0, 10)
        val pageableCaptor = ArgumentCaptor.forClass(Pageable::class.java)

        Mockito
            .`when`(userRoleRepository!!.findByUserId(eqArg(userId), anyPageable()))
            .thenReturn(PageImpl(listOf(userRole), pageable, 1))

        val result = roleService!!.getUserRoles(userId, pageable)

        Mockito.verify(userRoleRepository!!).findByUserId(eqArg(userId), capturePageable(pageableCaptor))
        val captured = pageableCaptor.value
        assertThat(captured.sort.isUnsorted).isTrue()
        assertThat(captured.sort.getOrderFor("role.name")).isNull()
        assertThat(captured.pageNumber).isEqualTo(0)
        assertThat(captured.pageSize).isEqualTo(10)
        assertThat(result.content).containsExactly(role1)
    }
}

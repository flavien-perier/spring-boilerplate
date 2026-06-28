package io.flavien.demo.api.user

import io.flavien.demo.api.generated.dto.ForgotPasswordDto
import io.flavien.demo.api.generated.dto.PermissionSettingPageDto
import io.flavien.demo.api.generated.dto.PermissionUpdateDto
import io.flavien.demo.api.generated.dto.RolePageDto
import io.flavien.demo.api.generated.dto.UserPageDto
import io.flavien.demo.api.permission.mapper.PermissionMapper
import io.flavien.demo.api.role.RoleDtoTestFactory
import io.flavien.demo.api.role.RoleTestFactory
import io.flavien.demo.api.role.mapper.RoleMapper
import io.flavien.demo.api.session.util.ContextUtil
import io.flavien.demo.api.user.mapper.UserMapper
import io.flavien.demo.api.user.mapper.UserUpdateMapper
import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.permission.model.PermissionSetting
import io.flavien.demo.domain.permission.service.PermissionService
import io.flavien.demo.domain.role.service.RoleService
import io.flavien.demo.domain.user.entity.User
import io.flavien.demo.domain.user.service.UserService
import io.mockk.every
import io.mockk.mockkObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class UserControllerTest {
    @InjectMocks
    var userController: UserController? = null

    @Mock
    var userService: UserService? = null

    @Mock
    var userMapper: UserMapper? = null

    @Mock
    var userUpdateMapper: UserUpdateMapper? = null

    @Mock
    var permissionService: PermissionService? = null

    @Mock
    var roleService: RoleService? = null

    @Mock
    var roleMapper: RoleMapper? = null

    @Mock
    var permissionMapper: PermissionMapper? = null

    companion object {
        private val USER_ID = UUID.fromString("00000000-0000-7000-8000-000000000001")
        private val ROLE_10_ID = UUID.fromString("00000000-0000-7000-8000-000000000010")
    }

    @Test
    fun `Test createUser`() {
        val userCreationDto = UserDtoTestFactory.initUserCreationDto()

        val response = userController!!.createUser(userCreationDto)

        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(ResponseEntity<Unit>(HttpStatus.NO_CONTENT))

        Mockito.verify(userService!!).create(userCreationDto.email, userCreationDto.password, userCreationDto.proofOfWork)
    }

    @Test
    fun `Test activateUser`() {
        val token = "token"

        val response = userController!!.activateUser(token)

        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(ResponseEntity<Unit>(HttpStatus.NO_CONTENT))

        Mockito.verify(userService!!).activate(token)
    }

    @Test
    fun `Test forgotPassword`() {
        val email = "perier@flavien.io"

        val response = userController!!.forgotPassword(ForgotPasswordDto(email))

        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(ResponseEntity<Unit>(HttpStatus.NO_CONTENT))

        Mockito.verify(userService!!).sendForgotPassword(email)
    }

    @Test
    fun `Test updatePassword`() {
        val changePasswordDto = UserDtoTestFactory.initChangePasswordDto()

        val response = userController!!.updatePassword(changePasswordDto)

        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(ResponseEntity<Unit>(HttpStatus.NO_CONTENT))

        Mockito.verify(userService!!).updatePassword(
            changePasswordDto.password,
            changePasswordDto.proofOfWork,
            changePasswordDto.token,
        )
    }

    @Test
    fun `Test getUser`() {
        val email = "perier@flavien.io"
        val user = UserTestFactory.initUser()
        val userDto = UserDtoTestFactory.initUserDto()

        Mockito.`when`(userService!!.getByEmail(email)).thenReturn(user)
        Mockito.`when`(userMapper!!.toUserDto(user)).thenReturn(userDto)

        val response = userController!!.getUser(email)

        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(ResponseEntity.ok(userDto))
    }

    @Test
    fun `Test updateUser`() {
        val email = "perier@flavien.io"
        val userUpdateAdminDto = UserDtoTestFactory.initUserUpdateAdminDto(enabled = false)
        val userUpdate = UserTestFactory.initUserUpdate(enabled = false)
        val userDto = UserDtoTestFactory.initUserDto()
        val user = UserTestFactory.initUser()

        Mockito.`when`(userUpdateMapper!!.fromUserUpdateAdminDto(userUpdateAdminDto)).thenReturn(userUpdate)
        Mockito.`when`(userService!!.updateByEmail(email, userUpdate)).thenReturn(user)
        Mockito.`when`(userMapper!!.toUserDto(user)).thenReturn(userDto)

        val response = userController!!.updateUser(email, userUpdateAdminDto)

        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(ResponseEntity.ok(userDto))
    }

    @Test
    fun `Test deleteUser`() {
        val email = "perier@flavien.io"

        val response = userController!!.deleteUser(email)

        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(ResponseEntity<Unit>(HttpStatus.NO_CONTENT))

        Mockito.verify(userService!!).deleteByEmail(email)
    }

    @Test
    fun `Test getCurrentUser`() {
        val userId = USER_ID
        val user = UserTestFactory.initUser()
        val userDto = UserDtoTestFactory.initUserDto()

        mockkObject(ContextUtil)
        every { ContextUtil.userId } returns userId

        Mockito.`when`(userService!!.getById(userId)).thenReturn(user)
        Mockito.`when`(userMapper!!.toUserDto(user)).thenReturn(userDto)

        val response = userController!!.getCurrentUser()

        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(ResponseEntity.ok(userDto))
    }

    @Test
    fun `Test updateCurrentUser`() {
        val userId = USER_ID
        val userUpdateDto = UserDtoTestFactory.initUserUpdateDto()
        val userUpdate = UserTestFactory.initUserUpdate()
        val userDto = UserDtoTestFactory.initUserDto()
        val user = UserTestFactory.initUser()

        mockkObject(ContextUtil)
        every { ContextUtil.userId } returns userId

        Mockito.`when`(userUpdateMapper!!.fromUserUpdateDto(userUpdateDto)).thenReturn(userUpdate)
        Mockito.`when`(userService!!.updateById(userId, userUpdate)).thenReturn(user)
        Mockito.`when`(userMapper!!.toUserDto(user)).thenReturn(userDto)

        val response = userController!!.updateCurrentUser(userUpdateDto)

        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(ResponseEntity.ok(userDto))
    }

    @Test
    fun `Test deleteCurrentUser`() {
        val userId = USER_ID

        mockkObject(ContextUtil)
        every { ContextUtil.userId } returns userId

        val response = userController!!.deleteCurrentUser()

        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(ResponseEntity<Unit>(HttpStatus.NO_CONTENT))

        Mockito.verify(userService!!).deleteById(userId)
    }

    @Test
    fun `Test findUsers`() {
        val query = "search"
        val page = 1
        val pageSize = 10
        val sortColumn = "email"
        val sortOrder = "ASC"

        val user1 = UserTestFactory.initUser()
        val user2 = UserTestFactory.initUser()
        val users = listOf<User>(user1, user2)

        val userDto1 = UserDtoTestFactory.initUserDto()
        val userDto2 = UserDtoTestFactory.initUserDto()

        val userPage = PageImpl<User>(users, PageRequest.of(0, pageSize), 2)

        val expectedUserPageDto =
            UserPageDto(
                totalElements = 2,
                totalPages = 1,
                number = 0,
                propertySize = pageSize,
                content = listOf(userDto1, userDto2),
            )

        Mockito.`when`(userService!!.find(query, page, pageSize, sortColumn, sortOrder)).thenReturn(userPage)
        Mockito.`when`(userMapper!!.toUserPageDto(userPage)).thenReturn(expectedUserPageDto)

        val response = userController!!.findUsers(query, page, pageSize, sortColumn, sortOrder)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.content).containsExactly(userDto1, userDto2)
        assertThat(response.body!!.totalElements).isEqualTo(2)
        assertThat(response.body!!.totalPages).isEqualTo(1)
        assertThat(response.body!!.propertySize).isEqualTo(pageSize)
    }

    @Test
    fun `Test getUserPermissionOverrides`() {
        val email = "perier@flavien.io"
        val page = 1
        val pageSize = 10
        val sortColumn = "permission"
        val sortOrder = "ASC"
        val pageable =
            PageRequest.of(
                0,
                pageSize,
                org.springframework.data.domain.Sort
                    .by(
                        org.springframework.data.domain.Sort.Direction
                            .fromString(sortOrder),
                        sortColumn,
                    ),
            )
        val user = UserTestFactory.initUser(id = USER_ID)
        val settings =
            listOf(
                PermissionSetting(PermissionEnum.MANAGE_ALL_USERS, true),
                PermissionSetting(PermissionEnum.MANAGE_ALL_ROLES, null),
            )
        val settingsPage = PageImpl(settings, pageable, 2)
        val dto1 = RoleDtoTestFactory.initPermissionSettingDto(permission = "MANAGE_ALL_USERS", allow = true)
        val dto2 = RoleDtoTestFactory.initPermissionSettingDto(permission = "MANAGE_ALL_ROLES", allow = null)
        val expectedPageDto =
            PermissionSettingPageDto(
                totalElements = 2,
                totalPages = 1,
                number = 0,
                propertySize = pageSize,
                content = listOf(dto1, dto2),
            )

        Mockito.`when`(userService!!.getByEmail(email)).thenReturn(user)
        Mockito.`when`(permissionService!!.getUserPermissionOverrides(user.id!!, pageable)).thenReturn(settingsPage)
        Mockito.`when`(permissionMapper!!.toPermissionSettingPageDto(settingsPage)).thenReturn(expectedPageDto)

        val response = userController!!.getUserPermissionOverrides(email, page, pageSize, sortColumn, sortOrder)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.content).hasSize(2)
        assertThat(response.body!!.content[0].permission).isEqualTo("MANAGE_ALL_USERS")
        assertThat(response.body!!.content[0].allow).isTrue()
        assertThat(response.body!!.content[1].permission).isEqualTo("MANAGE_ALL_ROLES")
        assertThat(response.body!!.content[1].allow).isNull()
        assertThat(response.body!!.totalElements).isEqualTo(2)
        assertThat(response.body!!.totalPages).isEqualTo(1)
        assertThat(response.body!!.propertySize).isEqualTo(pageSize)
    }

    @Test
    fun `Test setUserPermission`() {
        val email = "perier@flavien.io"
        val user = UserTestFactory.initUser(id = USER_ID)
        val updateDto = PermissionUpdateDto(true)

        Mockito.`when`(userService!!.getByEmail(email)).thenReturn(user)

        val response = userController!!.setUserPermission(email, "MANAGE_ALL_USERS", updateDto)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        Mockito.verify(permissionService!!).setUserPermission(user.id!!, PermissionEnum.MANAGE_ALL_USERS, true)
    }

    @Test
    fun `Test removeUserPermission`() {
        val email = "perier@flavien.io"
        val user = UserTestFactory.initUser(id = USER_ID)

        Mockito.`when`(userService!!.getByEmail(email)).thenReturn(user)

        val response = userController!!.removeUserPermission(email, "MANAGE_ALL_USERS")

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        Mockito.verify(permissionService!!).removeUserPermission(user.id!!, PermissionEnum.MANAGE_ALL_USERS)
    }

    @Test
    fun `Test getUserRoles`() {
        val email = "perier@flavien.io"
        val page = 1
        val pageSize = 10
        val sortColumn = "name"
        val sortOrder = "ASC"
        val pageable =
            PageRequest.of(
                0,
                pageSize,
                org.springframework.data.domain.Sort
                    .by(
                        org.springframework.data.domain.Sort.Direction
                            .fromString(sortOrder),
                        sortColumn,
                    ),
            )
        val user = UserTestFactory.initUser(id = USER_ID)
        val role1 = RoleTestFactory.initRole(id = UUID.fromString("00000000-0000-7000-8000-000000000001"), name = "G1")
        val role2 = RoleTestFactory.initRole(id = UUID.fromString("00000000-0000-7000-8000-000000000002"), name = "G2")
        val rolesPage = PageImpl(listOf(role1, role2), pageable, 2)
        val dto1 = RoleDtoTestFactory.initRoleDto(name = "G1")
        val dto2 = RoleDtoTestFactory.initRoleDto(name = "G2")
        val expectedPageDto =
            RolePageDto(
                totalElements = 2,
                totalPages = 1,
                number = 0,
                propertySize = pageSize,
                content = listOf(dto1, dto2),
            )

        Mockito.`when`(userService!!.getByEmail(email)).thenReturn(user)
        Mockito.`when`(roleService!!.getUserRoles(user.id!!, pageable)).thenReturn(rolesPage)
        Mockito.`when`(roleMapper!!.toRolePageDto(rolesPage)).thenReturn(expectedPageDto)

        val response = userController!!.getUserRoles(email, page, pageSize, sortColumn, sortOrder)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.content).containsExactly(dto1, dto2)
        assertThat(response.body!!.totalElements).isEqualTo(2)
        assertThat(response.body!!.totalPages).isEqualTo(1)
        assertThat(response.body!!.propertySize).isEqualTo(pageSize)
    }

    @Test
    fun `Test addUserToRole`() {
        val email = "perier@flavien.io"
        val user = UserTestFactory.initUser(id = USER_ID)

        Mockito.`when`(userService!!.getByEmail(email)).thenReturn(user)

        val response = userController!!.addUserToRole(email, ROLE_10_ID.toString())

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        Mockito.verify(roleService!!).addUserToRole(user.id!!, ROLE_10_ID)
    }

    @Test
    fun `Test removeUserFromRole`() {
        val email = "perier@flavien.io"
        val user = UserTestFactory.initUser(id = USER_ID)

        Mockito.`when`(userService!!.getByEmail(email)).thenReturn(user)

        val response = userController!!.removeUserFromRole(email, ROLE_10_ID.toString())

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        Mockito.verify(roleService!!).removeUserFromRole(user.id!!, ROLE_10_ID)
    }

    @Test
    fun `Test disableUserOtp`() {
        val email = "perier@flavien.io"
        val user = UserTestFactory.initUser(id = USER_ID)

        Mockito.`when`(userService!!.getByEmail(email)).thenReturn(user)

        val response = userController!!.disableUserOtp(email)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        Mockito.verify(userService!!).disableOtp(user.id!!)
    }

    @Test
    fun `Test sendUserPasswordReset`() {
        val email = "perier@flavien.io"

        val response = userController!!.sendUserPasswordReset(email)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        Mockito.verify(userService!!).sendForgotPassword(email)
    }
}

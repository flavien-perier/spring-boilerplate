package io.flavien.demo.api.user

import io.flavien.demo.api.generated.dto.ForgotPasswordDto
import io.flavien.demo.api.generated.dto.PermissionUpdateDto
import io.flavien.demo.api.generated.dto.UserPageDto
import io.flavien.demo.api.group.GroupDtoTestFactory
import io.flavien.demo.api.group.GroupTestFactory
import io.flavien.demo.api.group.mapper.GroupMapper
import io.flavien.demo.api.session.util.ContextUtil
import io.flavien.demo.api.user.mapper.UserMapper
import io.flavien.demo.api.user.mapper.UserUpdateMapper
import io.flavien.demo.domain.group.service.GroupService
import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.permission.model.PermissionSetting
import io.flavien.demo.domain.permission.service.PermissionService
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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

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
    var groupService: GroupService? = null

    @Mock
    var groupMapper: GroupMapper? = null

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

        Mockito.`when`(userService!!.get(email)).thenReturn(user)
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
        Mockito.`when`(userService!!.update(email, userUpdate)).thenReturn(user)
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

        Mockito.verify(userService!!).delete(email)
    }

    @Test
    fun `Test getCurrentUser`() {
        val userId = 1L
        val user = UserTestFactory.initUser()
        val userDto = UserDtoTestFactory.initUserDto()

        mockkObject(ContextUtil)
        every { ContextUtil.userId } returns userId

        Mockito.`when`(userService!!.get(userId)).thenReturn(user)
        Mockito.`when`(userMapper!!.toUserDto(user)).thenReturn(userDto)

        val response = userController!!.getCurrentUser()

        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(ResponseEntity.ok(userDto))
    }

    @Test
    fun `Test updateCurrentUser`() {
        val userId = 1L
        val userUpdateDto = UserDtoTestFactory.initUserUpdateDto()
        val userUpdate = UserTestFactory.initUserUpdate()
        val userDto = UserDtoTestFactory.initUserDto()
        val user = UserTestFactory.initUser()

        mockkObject(ContextUtil)
        every { ContextUtil.userId } returns userId

        Mockito.`when`(userUpdateMapper!!.fromUserUpdateDto(userUpdateDto)).thenReturn(userUpdate)
        Mockito.`when`(userService!!.update(userId, userUpdate)).thenReturn(user)
        Mockito.`when`(userMapper!!.toUserDto(user)).thenReturn(userDto)

        val response = userController!!.updateCurrentUser(userUpdateDto)

        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(ResponseEntity.ok(userDto))
    }

    @Test
    fun `Test deleteCurrentUser`() {
        val userId = 1L

        mockkObject(ContextUtil)
        every { ContextUtil.userId } returns userId

        val response = userController!!.deleteCurrentUser()

        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(ResponseEntity<Unit>(HttpStatus.NO_CONTENT))

        Mockito.verify(userService!!).delete(userId)
    }

    @Test
    fun `Test findUsers`() {
        val query = "search"
        val page = 0
        val pageSize = 10
        val sortColumn = "email"
        val sortOrder = "ASC"

        val user1 = UserTestFactory.initUser()
        val user2 = UserTestFactory.initUser()
        val users = listOf<User>(user1, user2)

        val userDto1 = UserDtoTestFactory.initUserDto()
        val userDto2 = UserDtoTestFactory.initUserDto()

        val userPage = PageImpl<User>(users)

        Mockito.`when`(userService!!.find(query, page, pageSize, sortColumn, sortOrder)).thenReturn(userPage)
        Mockito.`when`(userMapper!!.toUserDto(user1)).thenReturn(userDto1)
        Mockito.`when`(userMapper!!.toUserDto(user2)).thenReturn(userDto2)

        val response = userController!!.findUsers(query, page, pageSize, sortColumn, sortOrder)

        val expectedUserPageDto =
            UserPageDto(
                users.size.toLong(),
                1,
                listOf(userDto1, userDto2),
            )

        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(ResponseEntity<UserPageDto>(expectedUserPageDto, HttpStatus.OK))
    }

    @Test
    fun `Test getUserPermissionOverrides`() {
        val email = "perier@flavien.io"
        val user = UserTestFactory.initUser(id = 1L)
        val settings =
            listOf(
                PermissionSetting(PermissionEnum.MANAGE_ALL_USERS, true),
                PermissionSetting(PermissionEnum.MANAGE_ALL_GROUPS, null),
            )

        Mockito.`when`(userService!!.get(email)).thenReturn(user)
        Mockito.`when`(permissionService!!.getUserPermissionOverrides(1L)).thenReturn(settings)

        val response = userController!!.getUserPermissionOverrides(email)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).hasSize(2)
        assertThat(response.body!![0].permission).isEqualTo("MANAGE_ALL_USERS")
        assertThat(response.body!![0].allow).isTrue()
        assertThat(response.body!![1].permission).isEqualTo("MANAGE_ALL_GROUPS")
        assertThat(response.body!![1].allow).isNull()
    }

    @Test
    fun `Test setUserPermission`() {
        val email = "perier@flavien.io"
        val user = UserTestFactory.initUser(id = 1L)
        val updateDto = PermissionUpdateDto(true)

        Mockito.`when`(userService!!.get(email)).thenReturn(user)

        val response = userController!!.setUserPermission(email, "MANAGE_ALL_USERS", updateDto)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        Mockito.verify(permissionService!!).setUserPermission(1L, PermissionEnum.MANAGE_ALL_USERS, true)
    }

    @Test
    fun `Test removeUserPermission`() {
        val email = "perier@flavien.io"
        val user = UserTestFactory.initUser(id = 1L)

        Mockito.`when`(userService!!.get(email)).thenReturn(user)

        val response = userController!!.removeUserPermission(email, "MANAGE_ALL_USERS")

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        Mockito.verify(permissionService!!).removeUserPermission(1L, PermissionEnum.MANAGE_ALL_USERS)
    }

    @Test
    fun `Test getUserGroups`() {
        val email = "perier@flavien.io"
        val user = UserTestFactory.initUser(id = 1L)
        val group1 = GroupTestFactory.initGroup(id = 1L, name = "G1")
        val group2 = GroupTestFactory.initGroup(id = 2L, name = "G2")
        val dto1 = GroupDtoTestFactory.initGroupDto(id = 1L, name = "G1")
        val dto2 = GroupDtoTestFactory.initGroupDto(id = 2L, name = "G2")

        Mockito.`when`(userService!!.get(email)).thenReturn(user)
        Mockito.`when`(groupService!!.getUserGroups(1L)).thenReturn(listOf(group1, group2))
        Mockito.`when`(groupMapper!!.toGroupDto(group1)).thenReturn(dto1)
        Mockito.`when`(groupMapper!!.toGroupDto(group2)).thenReturn(dto2)

        val response = userController!!.getUserGroups(email)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).containsExactly(dto1, dto2)
    }

    @Test
    fun `Test addUserToGroup`() {
        val email = "perier@flavien.io"
        val user = UserTestFactory.initUser(id = 1L)

        Mockito.`when`(userService!!.get(email)).thenReturn(user)

        val response = userController!!.addUserToGroup(email, 10L)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        Mockito.verify(groupService!!).addUserToGroup(1L, 10L)
    }

    @Test
    fun `Test removeUserFromGroup`() {
        val email = "perier@flavien.io"
        val user = UserTestFactory.initUser(id = 1L)

        Mockito.`when`(userService!!.get(email)).thenReturn(user)

        val response = userController!!.removeUserFromGroup(email, 10L)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        Mockito.verify(groupService!!).removeUserFromGroup(1L, 10L)
    }

    @Test
    fun `Test disableUserOtp`() {
        val email = "perier@flavien.io"
        val user = UserTestFactory.initUser(id = 1L)

        Mockito.`when`(userService!!.get(email)).thenReturn(user)

        val response = userController!!.disableUserOtp(email)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        Mockito.verify(userService!!).disableOtp(1L)
    }

    @Test
    fun `Test sendUserPasswordReset`() {
        val email = "perier@flavien.io"

        val response = userController!!.sendUserPasswordReset(email)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        Mockito.verify(userService!!).sendForgotPassword(email)
    }
}

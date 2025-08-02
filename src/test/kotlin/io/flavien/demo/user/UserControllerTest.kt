package io.flavien.demo.user

import io.flavien.demo.dto.UserPageDto
import io.flavien.demo.session.util.ContextUtil
import io.flavien.demo.user.mapper.UserMapper
import io.flavien.demo.user.mapper.UserUpdateMapper
import io.flavien.demo.user.service.UserService
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

    @Test
    fun `Test createUser`() {
        // Given
        val userCreationDto = UserTestFactory.initUserCreationDto()

        // When
        val response = userController!!.createUser(userCreationDto)

        // Then
        assertThat(response).usingRecursiveComparison()
            .isEqualTo(ResponseEntity<Void>(HttpStatus.NO_CONTENT))

        Mockito.verify(userService!!).create(userCreationDto.email, userCreationDto.password, userCreationDto.proofOfWork)
    }

    @Test
    fun `Test activateUser`() {
        // Given
        val token = "token"

        // When
        val response = userController!!.activateUser(token)

        // Then
        assertThat(response).usingRecursiveComparison()
            .isEqualTo(ResponseEntity<Void>(HttpStatus.NO_CONTENT))

        Mockito.verify(userService!!).activate(token)
    }

    @Test
    fun `Test forgotPassword`() {
        // Given
        val email = "perier@flavien.io"

        // When
        val response = userController!!.forgotPassword(email)

        // Then
        assertThat(response).usingRecursiveComparison()
            .isEqualTo(ResponseEntity<Void>(HttpStatus.NO_CONTENT))

        Mockito.verify(userService!!).sendForgotPassword(email)
    }

    @Test
    fun `Test updatePassword`() {
        // Given
        val changePasswordDto = UserTestFactory.initChangePasswordDto()

        // When
        val response = userController!!.updatePassword(changePasswordDto)

        // Then
        assertThat(response).usingRecursiveComparison()
            .isEqualTo(ResponseEntity<Void>(HttpStatus.NO_CONTENT))

        Mockito.verify(userService!!).updatePassword(
            changePasswordDto.password,
            changePasswordDto.proofOfWork,
            changePasswordDto.token
        )
    }

    @Test
    fun `Test getUser`() {
        // Given
        val email = "perier@flavien.io"
        val user = UserTestFactory.initUser()
        val userDto = UserTestFactory.initUserDto()

        Mockito.`when`(userService!!.get(email)).thenReturn(user)
        Mockito.`when`(userMapper!!.toUserDto(user)).thenReturn(userDto)

        // When
        val response = userController!!.getUser(email)

        // Then
        assertThat(response).usingRecursiveComparison()
            .isEqualTo(ResponseEntity(userDto, HttpStatus.OK))
    }

    @Test
    fun `Test updateUser`() {
        // Given
        val email = "perier@flavien.io"
        val userUpdateAdminDto = UserTestFactory.initUserUpdateAdminDto()
        val userUpdate = UserTestFactory.initUserUpdate()
        val userDto = UserTestFactory.initUserDto()
        val user = UserTestFactory.initUser()

        Mockito.`when`(userUpdateMapper!!.fromUserUpdateAdminDto(userUpdateAdminDto)).thenReturn(userUpdate)
        Mockito.`when`(userService!!.update(email, userUpdate)).thenReturn(user)
        Mockito.`when`(userMapper!!.toUserDto(user)).thenReturn(userDto)

        // When
        val response = userController!!.updateUser(email, userUpdateAdminDto)

        // Then
        assertThat(response).usingRecursiveComparison()
            .isEqualTo(ResponseEntity(userDto, HttpStatus.OK))
    }

    @Test
    fun `Test deleteUser`() {
        // Given
        val email = "perier@flavien.io"

        // When
        val response = userController!!.deleteUser(email)

        // Then
        assertThat(response).usingRecursiveComparison()
            .isEqualTo(ResponseEntity<Void>(HttpStatus.NO_CONTENT))

        Mockito.verify(userService!!).delete(email)
    }

    @Test
    fun `Test getUserMe`() {
        // Given
        val userId = 1L
        val user = UserTestFactory.initUser()
        val userDto = UserTestFactory.initUserDto()

        mockkObject(ContextUtil)
        every { ContextUtil.userId } returns userId

        Mockito.`when`(userService!!.get(userId)).thenReturn(user)
        Mockito.`when`(userMapper!!.toUserDto(user)).thenReturn(userDto)

        // When
        val response = userController!!.getUserMe()

        // Then
        assertThat(response).usingRecursiveComparison()
            .isEqualTo(ResponseEntity(userDto, HttpStatus.OK))
    }

    @Test
    fun `Test updateUserMe`() {
        // Given
        val userId = 1L
        val userUpdateDto = UserTestFactory.initUserUpdateDto()
        val userUpdate = UserTestFactory.initUserUpdate()
        val userDto = UserTestFactory.initUserDto()
        val user = UserTestFactory.initUser()

        mockkObject(ContextUtil)
        every { ContextUtil.userId } returns userId

        Mockito.`when`(userUpdateMapper!!.fromUserUpdateDto(userUpdateDto)).thenReturn(userUpdate)
        Mockito.`when`(userService!!.update(userId, userUpdate)).thenReturn(user)
        Mockito.`when`(userMapper!!.toUserDto(user)).thenReturn(userDto)

        // When
        val response = userController!!.updateUserMe(userUpdateDto)

        // Then
        assertThat(response).usingRecursiveComparison()
            .isEqualTo(ResponseEntity(userDto, HttpStatus.OK))
    }

    @Test
    fun `Test deleteUserMe`() {
        // Given
        val userId = 1L

        mockkObject(ContextUtil)
        every { ContextUtil.userId } returns userId

        // When
        val response = userController!!.deleteUserMe()

        // Then
        assertThat(response).usingRecursiveComparison()
            .isEqualTo(ResponseEntity<Void>(HttpStatus.NO_CONTENT))

        Mockito.verify(userService!!).delete(userId)
    }

    @Test
    fun `Test findUsers`() {
        // Given
        val query = "search"
        val page = 0
        val pageSize = 10
        val sortColumn = "email"
        val sortOrder = "ASC"

        val user1 = UserTestFactory.initUser()
        val user2 = UserTestFactory.initUser()
        val users = listOf(user1, user2)

        val userDto1 = UserTestFactory.initUserDto()
        val userDto2 = UserTestFactory.initUserDto()

        val userPage = PageImpl(users)

        Mockito.`when`(userService!!.find(query, page, pageSize, sortColumn, sortOrder)).thenReturn(userPage)
        Mockito.`when`(userMapper!!.toUserDto(user1)).thenReturn(userDto1)
        Mockito.`when`(userMapper!!.toUserDto(user2)).thenReturn(userDto2)

        // When
        val response = userController!!.findUsers(query, page, pageSize, sortColumn, sortOrder)

        // Then
        val expectedUserPageDto = UserPageDto(
            users.size.toLong(),
            1,
            listOf(userDto1, userDto2)
        )

        assertThat(response).usingRecursiveComparison()
            .isEqualTo(ResponseEntity<UserPageDto>(expectedUserPageDto, HttpStatus.OK))
    }
}

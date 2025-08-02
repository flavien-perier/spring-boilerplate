package io.flavien.demo.user.service

import io.flavien.demo.core.util.RandomUtil
import io.flavien.demo.session.service.AccessTokenService
import io.flavien.demo.session.service.RefreshTokenService
import io.flavien.demo.session.util.PasswordUtil
import io.flavien.demo.user.UserTestFactory
import io.flavien.demo.user.model.UserRole
import io.flavien.demo.user.repository.UserRepository
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @InjectMocks
    lateinit var userService: UserService

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var userActivationService: UserActivationService

    @Mock
    lateinit var forgotPasswordService: ForgotPasswordService

    @Mock
    lateinit var accessTokenService: AccessTokenService

    @Mock
    lateinit var refreshTokenService: RefreshTokenService

    @Test
    fun `Should create user`() {
        // Given
        val email = "test@example.com"
        val password = "password"
        val proofOfWork = "proofOfWork"
        val salt = "randomSalt"
        val hashedPassword = "hashedPassword"
        val user = UserTestFactory.initUser(email, hashedPassword, "proofOfWork", salt, UserRole.USER, false)

        mockkObject(RandomUtil)
        every { RandomUtil.randomString(any()) } returns salt

        mockkObject(PasswordUtil)
        every { PasswordUtil.hashPassword(password, salt) } returns hashedPassword

        `when`(userRepository.existsByEmail(email)).thenReturn(false)
        `when`(userRepository.save(any())).thenReturn(user)

        // When
        val result = userService.create(email, password, proofOfWork)

        // Then
        verify(userRepository).existsByEmail(email)
        verify(userRepository).save(any())
        verify(userActivationService).sendActivationToken(user)

        assertEquals(email, result.email)
        assertEquals(hashedPassword, result.password)
        assertEquals(salt, result.passwordSalt)
        assertEquals(UserRole.USER, result.role)
        assertEquals(false, result.enabled)

        // Clean up
        unmockkObject(RandomUtil)
        unmockkObject(PasswordUtil)
    }

    @Test
    fun `Should activate a user after creation`() {
        // Given
        val token = "activationToken"
        val userId = 1L
        val userActivation = UserTestFactory.initUserActivation(token, userId)
        val user = UserTestFactory.initUser("test@example.com", "hashedPassword", "proofOfWork", "salt", UserRole.USER, false, userId)

        `when`(userActivationService.validate(token)).thenReturn(userActivation)
        `when`(userRepository.getUserById(userId)).thenReturn(Optional.of(user))

        // When
        userService.activate(token)

        // Then
        verify(userActivationService).validate(token)
        verify(userRepository).getUserById(userId)
        verify(userRepository).save(user)

        assertEquals(true, user.enabled)
    }

    @Test
    fun `Should send a forgotten password token by email`() {
        // Given
        val email = "test@example.com"
        val user = UserTestFactory.initUser(email, "hashedPassword", "proofOfWork", "salt", UserRole.USER, true)

        `when`(userRepository.getByEmail(email)).thenReturn(Optional.of(user))

        // When
        userService.sendForgotPassword(email)

        // Then
        verify(userRepository).getByEmail(email)
        verify(forgotPasswordService).sendForgotPasswordToken(user)
    }

    @Test
    fun `Should update password with forgotten password token`() {
        // Given
        val token = "forgotPasswordToken"
        val newPassword = "newPassword"
        val proofOfWork = "proofOfWork"
        val userId = 1L
        val forgotPassword = UserTestFactory.initForgotPassword(token, userId)
        val user = UserTestFactory.initUser("test@example.com", "oldHashedPassword", "proofOfWork", "oldSalt", UserRole.USER, false, userId)
        val newSalt = "newSalt"
        val newHashedPassword = "newHashedPassword"

        mockkObject(RandomUtil)
        every { RandomUtil.randomString(any()) } returns newSalt

        mockkObject(PasswordUtil)
        every { PasswordUtil.hashPassword(newPassword, newSalt) } returns newHashedPassword

        `when`(forgotPasswordService.validate(token)).thenReturn(forgotPassword)
        `when`(userRepository.getUserById(userId)).thenReturn(Optional.of(user))

        // When
        userService.updatePassword(newPassword, proofOfWork, token)

        // Then
        verify(forgotPasswordService).validate(token)
        verify(userRepository).getUserById(userId)
        verify(userRepository).save(user)

        assertEquals(newHashedPassword, user.password)
        assertEquals(newSalt, user.passwordSalt)
        assertEquals(true, user.enabled)

        // Clean up
        unmockkObject(RandomUtil)
        unmockkObject(PasswordUtil)
    }

    @Test
    fun `Should disable a user`() {
        // Given
        val userId = 1L
        val user = UserTestFactory.initUser("test@example.com", "hashedPassword", "proofOfWork", "salt", UserRole.USER, true, userId)

        `when`(userRepository.getUserById(userId)).thenReturn(Optional.of(user))

        // When
        userService.disables(userId)

        // Then
        verify(userRepository).getUserById(userId)
        verify(accessTokenService).deleteByUserId(userId)
        verify(refreshTokenService).deleteByUserId(userId)
        verify(userActivationService).deleteByUserId(userId)
        verify(forgotPasswordService).deleteByUserId(userId)
        verify(userRepository).save(user)

        assertEquals(false, user.enabled)
    }

    @Test
    fun `Should update user information based on userId`() {
        // Given
        val userId = 1L
        val user = UserTestFactory.initUser("old@example.com", "oldHashedPassword", "proofOfWork", "oldSalt", UserRole.USER, true, userId)

        val newEmail = "new@example.com"
        val newPassword = "newPassword"
        val proofOfWork = "proofOfWork"
        val newRole = UserRole.ADMIN
        val userUpdate = UserTestFactory.initUserUpdate(newEmail, newPassword, proofOfWork, newRole)

        val newSalt = "newSalt"
        val newHashedPassword = "newHashedPassword"

        mockkObject(RandomUtil)
        every { RandomUtil.randomString(any()) } returns newSalt

        mockkObject(PasswordUtil)
        every { PasswordUtil.hashPassword(newPassword, newSalt) } returns newHashedPassword

        `when`(userRepository.getUserById(userId)).thenReturn(Optional.of(user))
        `when`(userRepository.existsByEmail(newEmail)).thenReturn(false)

        // When
        val result = userService.update(userId, userUpdate)

        // Then
        verify(userRepository).getUserById(userId)
        verify(userRepository).existsByEmail(newEmail)
        verify(userRepository).save(user)

        assertEquals(newEmail, result.email)
        assertEquals(newHashedPassword, result.password)
        assertEquals(newSalt, result.passwordSalt)
        assertEquals(newRole, result.role)

        // Clean up
        unmockkObject(RandomUtil)
        unmockkObject(PasswordUtil)
    }

    @Test
    fun `Should update user information based on email`() {
        // Given
        val oldEmail = "old@example.com"
        val user = UserTestFactory.initUser(oldEmail, "oldHashedPassword", "proofOfWork", "oldSalt", UserRole.USER, true, 1L)

        val newEmail = "new@example.com"
        val newPassword = "newPassword"
        val proofOfWork = "proofOfWork"
        val newRole = UserRole.ADMIN
        val userUpdate = UserTestFactory.initUserUpdate(newEmail, newPassword, proofOfWork, newRole)

        val newSalt = "newSalt"
        val newHashedPassword = "newHashedPassword"

        mockkObject(RandomUtil)
        every { RandomUtil.randomString(any()) } returns newSalt

        mockkObject(PasswordUtil)
        every { PasswordUtil.hashPassword(newPassword, newSalt) } returns newHashedPassword

        `when`(userRepository.getByEmail(oldEmail)).thenReturn(Optional.of(user))
        `when`(userRepository.existsByEmail(newEmail)).thenReturn(false)

        // When
        val result = userService.update(oldEmail, userUpdate)

        // Then
        verify(userRepository).getByEmail(oldEmail)
        verify(userRepository).existsByEmail(newEmail)
        verify(userRepository).save(user)

        assertEquals(newEmail, result.email)
        assertEquals(newHashedPassword, result.password)
        assertEquals(newSalt, result.passwordSalt)
        assertEquals(newRole, result.role)

        // Clean up
        unmockkObject(RandomUtil)
        unmockkObject(PasswordUtil)
    }

    @Test
    fun `Should indicate whether a user exists according to its id`() {
        // Given
        val userId = 1L
        `when`(userRepository.existsById(userId)).thenReturn(true)

        // When
        val result = userService.exists(userId)

        // Then
        verify(userRepository).existsById(userId)
        assertEquals(true, result)
    }

    @Test
    fun `Should indicate if a user does not exist according to its id`() {
        // Given
        val userId = 1L
        `when`(userRepository.existsById(userId)).thenReturn(false)

        // When
        val result = userService.exists(userId)

        // Then
        verify(userRepository).existsById(userId)
        assertEquals(false, result)
    }

    @Test
    fun `Should return a user according to his id`() {
        // Given
        val userId = 1L
        val user = UserTestFactory.initUser("test@example.com", "hashedPassword", "proofOfWork", "salt", UserRole.USER, true, userId)

        `when`(userRepository.getUserById(userId)).thenReturn(Optional.of(user))

        // When
        val result = userService.get(userId)

        // Then
        verify(userRepository).getUserById(userId)
        assertEquals(user, result)
    }

    @Test
    fun `Should return a user according to his email`() {
        // Given
        val email = "test@example.com"
        val user = UserTestFactory.initUser(email, "hashedPassword", "proofOfWork", "salt", UserRole.USER, true, 1L)

        `when`(userRepository.getByEmail(email)).thenReturn(Optional.of(user))

        // When
        val result = userService.get(email)

        // Then
        verify(userRepository).getByEmail(email)
        assertEquals(user, result)
    }

    @Test
    fun `Should delete a user by id`() {
        // Given
        val userId = 1L
        val user = UserTestFactory.initUser("test@example.com", "hashedPassword", "proofOfWork", "salt", UserRole.USER, true, userId)

        `when`(userRepository.getUserById(userId)).thenReturn(Optional.of(user))

        // When
        userService.delete(userId)

        // Then
        verify(userRepository).getUserById(userId)
        verify(accessTokenService).deleteByUserId(userId)
        verify(refreshTokenService).deleteByUserId(userId)
        verify(userActivationService).deleteByUserId(userId)
        verify(forgotPasswordService).deleteByUserId(userId)
        verify(userRepository).delete(user)
    }

    @Test
    fun `Should delete a user by email`() {
        // Given
        val email = "test@example.com"
        val userId = 1L
        val user = UserTestFactory.initUser(email, "hashedPassword", "proofOfWork", "salt", UserRole.USER, true, userId)

        `when`(userRepository.getByEmail(email)).thenReturn(Optional.of(user))

        // When
        userService.delete(email)

        // Then
        verify(userRepository).getByEmail(email)
        verify(accessTokenService).deleteByUserId(userId)
        verify(refreshTokenService).deleteByUserId(userId)
        verify(userActivationService).deleteByUserId(userId)
        verify(forgotPasswordService).deleteByUserId(userId)
        verify(userRepository).delete(user)
    }
}

package io.flavien.demo.user.service

import io.flavien.demo.session.service.AccessTokenService
import io.flavien.demo.session.service.RefreshTokenService
import io.flavien.demo.user.repository.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @InjectMocks
    var userService: UserService? = null

    @Mock
    var userRepository: UserRepository? = null

    @Mock
    var userActivationService: UserActivationService? = null

    @Mock
    var forgotPasswordService: ForgotPasswordService? = null

    @Mock
    var accessTokenService: AccessTokenService? = null

    @Mock
    var refreshTokenService: RefreshTokenService? = null

    // TODO: Implement tests
    @Test
    fun `Should create user`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should activate a user after creation`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should send a forgotten password token by email`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should update password with forgotten password token`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should disable a user`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should update user information based on userId`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should update user information based on email`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should indicate whether a user exists according to its id`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should indicate if a user does not exist according to its id`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should return a user according to his id`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should return a user according to his email`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should delete a user by id`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should delete a user by email`() {
        // Given

        // When

        // Then
    }
}
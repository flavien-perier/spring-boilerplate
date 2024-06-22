package io.flavien.demo.session.service

import io.flavien.demo.user.service.UserService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class SessionServiceTest {

    @InjectMocks
    var sessionService: SessionService? = null

    @Mock
    var refreshTokenService: RefreshTokenService? = null

    @Mock
    var accessTokenService: AccessTokenService? = null

    @Mock
    var userService: UserService? = null

    // TODO: Implement tests
    @Test
    fun `Should login`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should login failed (User is disabled)`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should login failed (Password is invalid)`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should renew the session`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should renew the session (The user does not correspond to the one indicated in the token)`() {
        // Given

        // When

        // Then
    }
}
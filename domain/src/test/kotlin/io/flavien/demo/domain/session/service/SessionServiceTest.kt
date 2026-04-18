package io.flavien.demo.domain.session.service

import io.flavien.demo.domain.session.SessionTestFactory
import io.flavien.demo.domain.session.exception.BadPasswordException
import io.flavien.demo.domain.session.exception.BadRefreshTokenException
import io.flavien.demo.domain.session.exception.OtpRequiredException
import io.flavien.demo.domain.session.exception.UserIsDisabledException
import io.flavien.demo.domain.user.UserTestFactory
import io.flavien.demo.domain.user.repository.UserRepository
import io.flavien.demo.domain.user.service.UserService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
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

    @Mock
    var userRepository: UserRepository? = null

    @Mock
    var passwordService: PasswordService? = null

    @Mock
    var otpService: OtpService? = null

    @Test
    fun `Should login`() {
        // Given
        val email = "perier@flavien.io"
        val password = "Password123!"
        val proofOfWork = "proofOfWork"
        val user = UserTestFactory.initUser().copy(id = 1L)
        val refreshToken = SessionTestFactory.initRefreshToken()
        val accessToken =
            SessionTestFactory.initAccessToken(
                userId = user.id!!,
                role = user.role,
                refreshTokenId = refreshToken.id,
            )

        `when`(userService!!.get(email)).thenReturn(user)
        `when`(passwordService!!.testPassword(password, user.passwordSalt, user.password)).thenReturn(true)
        `when`(userRepository!!.save(user)).thenReturn(user)
        `when`(refreshTokenService!!.create(user.id!!, user.role)).thenReturn(refreshToken)
        `when`(accessTokenService!!.create(refreshToken)).thenReturn(accessToken)

        // When
        val result = sessionService!!.login(email, password, proofOfWork)

        // Then
        verify(userService!!).get(email)
        verify(passwordService!!).testPassword(password, user.passwordSalt, user.password)
        verify(userRepository!!).save(user)
        verify(refreshTokenService!!).create(user.id!!, user.role)
        verify(accessTokenService!!).create(refreshToken)

        assertEquals(refreshToken, result.refreshToken)
        assertEquals(accessToken, result.accessToken)
    }

    @Test
    fun `Should login failed (User is disabled)`() {
        // Given
        val email = "perier@flavien.io"
        val password = "Password123!"
        val proofOfWork = "proofOfWork"
        val user = UserTestFactory.initUser().copy(enabled = false)

        `when`(userService!!.get(email)).thenReturn(user)

        // When/Then
        assertThrows(UserIsDisabledException::class.java) {
            sessionService!!.login(email, password, proofOfWork)
        }

        verify(userService!!).get(email)
    }

    @Test
    fun `Should login failed (Password is invalid)`() {
        // Given
        val email = "perier@flavien.io"
        val password = "wrong-password"
        val proofOfWork = "proofOfWork"
        val user = UserTestFactory.initUser()

        `when`(userService!!.get(email)).thenReturn(user)
        `when`(passwordService!!.testPassword(password, user.passwordSalt, user.password)).thenReturn(false)

        // When/Then
        assertThrows(BadPasswordException::class.java) {
            sessionService!!.login(email, password, proofOfWork)
        }

        verify(userService!!).get(email)
        verify(passwordService!!).testPassword(password, user.passwordSalt, user.password)
    }

    @Test
    fun `Should throw OtpRequiredException when OTP is active and no code provided`() {
        // Given
        val email = "perier@flavien.io"
        val password = "Password123!"
        val proofOfWork = "proofOfWork"
        val user = UserTestFactory.initUser().copy(otpSecret = "JBSWY3DPEHPK3PXP")

        `when`(userService!!.get(email)).thenReturn(user)
        `when`(passwordService!!.testPassword(password, user.passwordSalt, user.password)).thenReturn(true)

        // When/Then
        assertThrows(OtpRequiredException::class.java) {
            sessionService!!.login(email, password, proofOfWork, null)
        }

        verify(userService!!).get(email)
        verify(passwordService!!).testPassword(password, user.passwordSalt, user.password)
    }

    @Test
    fun `Should login successfully when OTP is active and valid code is provided`() {
        // Given
        val email = "perier@flavien.io"
        val password = "Password123!"
        val proofOfWork = "proofOfWork"
        val otpCode = "123456"
        val user = UserTestFactory.initUser().copy(id = 1L, otpSecret = "JBSWY3DPEHPK3PXP")
        val refreshToken = SessionTestFactory.initRefreshToken()
        val accessToken =
            SessionTestFactory.initAccessToken(
                userId = user.id!!,
                role = user.role,
                refreshTokenId = refreshToken.id,
            )

        `when`(userService!!.get(email)).thenReturn(user)
        `when`(passwordService!!.testPassword(password, user.passwordSalt, user.password)).thenReturn(true)
        `when`(otpService!!.validateTOTP("JBSWY3DPEHPK3PXP", otpCode)).thenReturn(true)
        `when`(userRepository!!.save(user)).thenReturn(user)
        `when`(refreshTokenService!!.create(user.id!!, user.role)).thenReturn(refreshToken)
        `when`(accessTokenService!!.create(refreshToken)).thenReturn(accessToken)

        // When
        val result = sessionService!!.login(email, password, proofOfWork, otpCode)

        // Then
        verify(userService!!).get(email)
        verify(passwordService!!).testPassword(password, user.passwordSalt, user.password)
        verify(otpService!!).validateTOTP("JBSWY3DPEHPK3PXP", otpCode)
        verify(userRepository!!).save(user)
        verify(refreshTokenService!!).create(user.id!!, user.role)
        verify(accessTokenService!!).create(refreshToken)

        assertEquals(refreshToken, result.refreshToken)
        assertEquals(accessToken, result.accessToken)
    }

    @Test
    fun `Should renew the session`() {
        // Given
        val email = "perier@flavien.io"
        val refreshTokenId = "refresh-token-id"
        val user = UserTestFactory.initUser().copy(id = 1L)
        val refreshToken = SessionTestFactory.initRefreshToken().copy(id = refreshTokenId, userId = user.id!!)
        val accessToken =
            SessionTestFactory.initAccessToken(
                userId = user.id!!,
                role = user.role,
                refreshTokenId = refreshTokenId,
            )

        `when`(refreshTokenService!!.get(refreshTokenId)).thenReturn(refreshToken)
        `when`(userService!!.get(email)).thenReturn(user)
        `when`(accessTokenService!!.create(refreshToken)).thenReturn(accessToken)

        // When
        val result = sessionService!!.renew(email, refreshTokenId)

        // Then
        verify(refreshTokenService!!).get(refreshTokenId)
        verify(userService!!).get(email)
        verify(accessTokenService!!).create(refreshToken)

        assertEquals(null, result.refreshToken)
        assertEquals(accessToken, result.accessToken)
    }

    @Test
    fun `Should renew the session (The user does not correspond to the one indicated in the token)`() {
        // Given
        val email = "perier@flavien.io"
        val refreshTokenId = "refresh-token-id"
        val user = UserTestFactory.initUser().copy(id = 1L)
        val differentUserId = user.id!! + 1
        val refreshToken = SessionTestFactory.initRefreshToken().copy(id = refreshTokenId, userId = differentUserId)

        `when`(refreshTokenService!!.get(refreshTokenId)).thenReturn(refreshToken)
        `when`(userService!!.get(email)).thenReturn(user)

        // When/Then
        assertThrows(BadRefreshTokenException::class.java) {
            sessionService!!.renew(email, refreshTokenId)
        }

        verify(refreshTokenService!!).get(refreshTokenId)
        verify(userService!!).get(email)
    }
}

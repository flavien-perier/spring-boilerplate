package io.flavien.demo.domain.user.service

import io.flavien.demo.domain.session.entity.OtpPending
import io.flavien.demo.domain.session.exception.OtpAlreadyConfiguredException
import io.flavien.demo.domain.session.exception.OtpNotPendingException
import io.flavien.demo.domain.session.exception.OtpRequiredException
import io.flavien.demo.domain.session.repository.OtpPendingRepository
import io.flavien.demo.domain.session.service.AccessTokenService
import io.flavien.demo.domain.session.service.OtpService
import io.flavien.demo.domain.session.service.PasswordService
import io.flavien.demo.domain.session.service.RefreshTokenService
import io.flavien.demo.domain.user.UserTestFactory
import io.flavien.demo.domain.user.entity.User
import io.flavien.demo.domain.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class UserOtpTest {
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

    @Mock
    var passwordService: PasswordService? = null

    @Mock
    var otpService: OtpService? = null

    @Mock
    var otpPendingRepository: OtpPendingRepository? = null

    // -------------------------------------------------------------------------
    // setupOtp
    // -------------------------------------------------------------------------

    @Test
    fun `setupOtp - should generate secret and return provisioning URI`() {
        // Given
        val user = UserTestFactory.initUser().copy(id = 1L)
        `when`(userRepository!!.getUserById(1L)).thenReturn(Optional.of(user))
        `when`(otpPendingRepository!!.existsById("1")).thenReturn(false)
        `when`(otpService!!.generateSecret()).thenReturn("JBSWY3DPEHPK3PXP")
        val pending = OtpPending("1", "JBSWY3DPEHPK3PXP")
        `when`(otpPendingRepository!!.save(any(OtpPending::class.java))).thenReturn(pending)

        // When
        val result = userService!!.setupOtp(1L)

        // Then
        assertThat(result).contains("secret=JBSWY3DPEHPK3PXP")
        verify(otpPendingRepository!!).save(OtpPending("1", "JBSWY3DPEHPK3PXP"))
    }

    @Test
    fun `setupOtp - should throw OtpAlreadyConfiguredException when OTP active in DB`() {
        // Given
        val user = UserTestFactory.initUser().copy(id = 1L, otpSecret = "EXISTINGSECRET")
        `when`(userRepository!!.getUserById(1L)).thenReturn(Optional.of(user))

        // When/Then
        assertThatThrownBy { userService!!.setupOtp(1L) }
            .isInstanceOf(OtpAlreadyConfiguredException::class.java)

        verify(otpPendingRepository!!, never()).existsById(any())
        verify(otpPendingRepository!!, never()).save(any(OtpPending::class.java))
    }

    @Test
    fun `setupOtp - should throw OtpAlreadyConfiguredException when pending in Redis`() {
        // Given
        val user = UserTestFactory.initUser().copy(id = 1L)
        `when`(userRepository!!.getUserById(1L)).thenReturn(Optional.of(user))
        `when`(otpPendingRepository!!.existsById("1")).thenReturn(true)

        // When/Then
        assertThatThrownBy { userService!!.setupOtp(1L) }
            .isInstanceOf(OtpAlreadyConfiguredException::class.java)

        verify(otpPendingRepository!!).existsById("1")
        verify(otpPendingRepository!!, never()).save(any(OtpPending::class.java))
    }

    // -------------------------------------------------------------------------
    // confirmOtp
    // -------------------------------------------------------------------------

    @Test
    fun `confirmOtp - should save secret to DB and delete from Redis`() {
        // Given
        val pending = OtpPending("1", "JBSWY3DPEHPK3PXP")
        val user = UserTestFactory.initUser().copy(id = 1L)
        `when`(otpPendingRepository!!.findById("1")).thenReturn(Optional.of(pending))
        `when`(otpService!!.validateTOTP("JBSWY3DPEHPK3PXP", "123456")).thenReturn(true)
        `when`(userRepository!!.getUserById(1L)).thenReturn(Optional.of(user))
        `when`(userRepository!!.save(any(User::class.java))).thenReturn(user)

        // When
        userService!!.confirmOtp(1L, "123456")

        // Then
        verify(userRepository!!).save(
            org.mockito.ArgumentMatchers.argThat { u: User -> u.otpSecret == "JBSWY3DPEHPK3PXP" },
        )
        verify(otpPendingRepository!!).deleteById("1")
    }

    @Test
    fun `confirmOtp - should throw OtpNotPendingException when no pending entry`() {
        // Given
        `when`(otpPendingRepository!!.findById("1")).thenReturn(Optional.empty())

        // When/Then
        assertThatThrownBy { userService!!.confirmOtp(1L, "123456") }
            .isInstanceOf(OtpNotPendingException::class.java)

        verify(otpPendingRepository!!).findById("1")
        verify(userRepository!!, never()).save(any(User::class.java))
    }

    @Test
    fun `confirmOtp - should throw OtpRequiredException when OTP code is invalid`() {
        // Given
        val pending = OtpPending("1", "JBSWY3DPEHPK3PXP")
        `when`(otpPendingRepository!!.findById("1")).thenReturn(Optional.of(pending))
        `when`(otpService!!.validateTOTP("JBSWY3DPEHPK3PXP", "000000")).thenReturn(false)

        // When/Then
        assertThatThrownBy { userService!!.confirmOtp(1L, "000000") }
            .isInstanceOf(OtpRequiredException::class.java)

        verify(otpPendingRepository!!).findById("1")
        verify(otpService!!).validateTOTP("JBSWY3DPEHPK3PXP", "000000")
        verify(userRepository!!, never()).save(any(User::class.java))
    }

    // -------------------------------------------------------------------------
    // disableOtp
    // -------------------------------------------------------------------------

    @Test
    fun `disableOtp - should clear OTP secret from DB and cleanup Redis`() {
        // Given
        val user = UserTestFactory.initUser().copy(id = 1L, otpSecret = "JBSWY3DPEHPK3PXP")
        `when`(userRepository!!.getUserById(1L)).thenReturn(Optional.of(user))
        `when`(userRepository!!.save(any(User::class.java))).thenReturn(user)

        // When
        userService!!.disableOtp(1L)

        // Then
        verify(userRepository!!).save(
            org.mockito.ArgumentMatchers.argThat { u: User -> u.otpSecret == null },
        )
        verify(otpPendingRepository!!).deleteById("1")
    }

    @Test
    fun `disableOtp - should be idempotent when OTP not active`() {
        // Given
        val user = UserTestFactory.initUser().copy(id = 1L)
        `when`(userRepository!!.getUserById(1L)).thenReturn(Optional.of(user))

        // When
        userService!!.disableOtp(1L)

        // Then
        verify(userRepository!!, never()).save(any(User::class.java))
        verify(otpPendingRepository!!).deleteById("1")
    }
}

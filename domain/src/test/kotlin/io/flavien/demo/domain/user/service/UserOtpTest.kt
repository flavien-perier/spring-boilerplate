package io.flavien.demo.domain.user.service

import io.flavien.demo.domain.permission.service.PermissionService
import io.flavien.demo.domain.role.service.RoleService
import io.flavien.demo.domain.session.entity.OtpPending
import io.flavien.demo.domain.session.exception.InvalidOtpException
import io.flavien.demo.domain.session.exception.OtpAlreadyConfiguredException
import io.flavien.demo.domain.session.exception.OtpNotPendingException
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
import org.mockito.Mockito.any
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional
import java.util.UUID

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

    @Mock
    var roleService: RoleService? = null

    @Mock
    var permissionService: PermissionService? = null

    private val userId = UUID.fromString("00000000-0000-0000-0000-00000000000a")
    private val userIdStr = userId.toString()

    // -------------------------------------------------------------------------
    // setupOtp
    // -------------------------------------------------------------------------

    @Test
    fun `setupOtp - should generate secret and return provisioning URI`() {
        val user = UserTestFactory.initUser().copy(id = userId)
        `when`(userRepository!!.getUserById(userId)).thenReturn(Optional.of(user))
        `when`(otpPendingRepository!!.existsById(userIdStr)).thenReturn(false)
        `when`(otpService!!.generateSecret()).thenReturn("JBSWY3DPEHPK3PXP")
        val pending = OtpPending(userIdStr, "JBSWY3DPEHPK3PXP")
        `when`(otpPendingRepository!!.save(any(OtpPending::class.java))).thenReturn(pending)

        val result = userService!!.setupOtp(userId)

        assertThat(result).contains("secret=JBSWY3DPEHPK3PXP")
        verify(otpPendingRepository!!).save(OtpPending(userIdStr, "JBSWY3DPEHPK3PXP"))
    }

    @Test
    fun `setupOtp - should throw OtpAlreadyConfiguredException when OTP active in DB`() {
        val user = UserTestFactory.initUser().copy(id = userId, otpSecret = "EXISTINGSECRET")
        `when`(userRepository!!.getUserById(userId)).thenReturn(Optional.of(user))

        assertThatThrownBy { userService!!.setupOtp(userId) }
            .isInstanceOf(OtpAlreadyConfiguredException::class.java)

        verify(otpPendingRepository!!, never()).existsById(any())
        verify(otpPendingRepository!!, never()).save(any(OtpPending::class.java))
    }

    @Test
    fun `setupOtp - should throw OtpAlreadyConfiguredException when pending in Redis`() {
        val user = UserTestFactory.initUser().copy(id = userId)
        `when`(userRepository!!.getUserById(userId)).thenReturn(Optional.of(user))
        `when`(otpPendingRepository!!.existsById(userIdStr)).thenReturn(true)

        assertThatThrownBy { userService!!.setupOtp(userId) }
            .isInstanceOf(OtpAlreadyConfiguredException::class.java)

        verify(otpPendingRepository!!).existsById(userIdStr)
        verify(otpPendingRepository!!, never()).save(any(OtpPending::class.java))
    }

    // -------------------------------------------------------------------------
    // confirmOtp
    // -------------------------------------------------------------------------

    @Test
    fun `confirmOtp - should save secret to DB and delete from Redis`() {
        val pending = OtpPending(userIdStr, "JBSWY3DPEHPK3PXP")
        val user = UserTestFactory.initUser().copy(id = userId)
        `when`(otpPendingRepository!!.findById(userIdStr)).thenReturn(Optional.of(pending))
        `when`(otpService!!.validateTOTP("JBSWY3DPEHPK3PXP", "123456")).thenReturn(true)
        `when`(userRepository!!.getUserById(userId)).thenReturn(Optional.of(user))
        `when`(userRepository!!.save(any(User::class.java))).thenReturn(user)

        userService!!.confirmOtp(userId, "123456")

        verify(userRepository!!).save(
            org.mockito.ArgumentMatchers.argThat { u: User -> u.otpSecret == "JBSWY3DPEHPK3PXP" },
        )
        verify(otpPendingRepository!!).deleteById(userIdStr)
    }

    @Test
    fun `confirmOtp - should throw OtpNotPendingException when no pending entry`() {
        `when`(otpPendingRepository!!.findById(userIdStr)).thenReturn(Optional.empty())

        assertThatThrownBy { userService!!.confirmOtp(userId, "123456") }
            .isInstanceOf(OtpNotPendingException::class.java)

        verify(otpPendingRepository!!).findById(userIdStr)
        verify(userRepository!!, never()).save(any(User::class.java))
    }

    @Test
    fun `confirmOtp - should throw InvalidOtpException when OTP code is invalid`() {
        val pending = OtpPending(userIdStr, "JBSWY3DPEHPK3PXP")
        `when`(otpPendingRepository!!.findById(userIdStr)).thenReturn(Optional.of(pending))
        `when`(otpService!!.validateTOTP("JBSWY3DPEHPK3PXP", "000000")).thenReturn(false)

        assertThatThrownBy { userService!!.confirmOtp(userId, "000000") }
            .isInstanceOf(InvalidOtpException::class.java)

        verify(otpPendingRepository!!).findById(userIdStr)
        verify(otpService!!).validateTOTP("JBSWY3DPEHPK3PXP", "000000")
        verify(userRepository!!, never()).save(any(User::class.java))
    }

    // -------------------------------------------------------------------------
    // disableOtp
    // -------------------------------------------------------------------------

    @Test
    fun `disableOtp - should clear OTP secret from DB and cleanup Redis`() {
        val user = UserTestFactory.initUser().copy(id = userId, otpSecret = "JBSWY3DPEHPK3PXP")
        `when`(userRepository!!.getUserById(userId)).thenReturn(Optional.of(user))
        `when`(userRepository!!.save(any(User::class.java))).thenReturn(user)

        userService!!.disableOtp(userId)

        verify(userRepository!!).save(
            org.mockito.ArgumentMatchers.argThat { u: User -> u.otpSecret == null },
        )
        verify(otpPendingRepository!!).deleteById(userIdStr)
    }

    @Test
    fun `disableOtp - should be idempotent when OTP not active`() {
        val user = UserTestFactory.initUser().copy(id = userId)
        `when`(userRepository!!.getUserById(userId)).thenReturn(Optional.of(user))

        userService!!.disableOtp(userId)

        verify(userRepository!!, never()).save(any(User::class.java))
        verify(otpPendingRepository!!).deleteById(userIdStr)
    }
}

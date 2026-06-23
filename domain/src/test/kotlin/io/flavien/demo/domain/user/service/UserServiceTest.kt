package io.flavien.demo.domain.user.service

import io.flavien.demo.domain.group.service.GroupService
import io.flavien.demo.domain.permission.service.PermissionService
import io.flavien.demo.domain.session.repository.OtpPendingRepository
import io.flavien.demo.domain.session.service.AccessTokenService
import io.flavien.demo.domain.session.service.OtpService
import io.flavien.demo.domain.session.service.PasswordService
import io.flavien.demo.domain.session.service.RefreshTokenService
import io.flavien.demo.domain.user.UserTestFactory
import io.flavien.demo.domain.user.entity.User
import io.flavien.demo.domain.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

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

    @Mock
    var passwordService: PasswordService? = null

    @Mock
    var otpService: OtpService? = null

    @Mock
    var otpPendingRepository: OtpPendingRepository? = null

    @Mock
    var groupService: GroupService? = null

    @Mock
    var permissionService: PermissionService? = null

    // -------------------------------------------------------------------------
    // update
    // -------------------------------------------------------------------------

    @Test
    fun `update - should enable user when enabled is true`() {
        val user = UserTestFactory.initUser(enabled = false).copy(id = 1L)
        `when`(userRepository!!.getUserById(1L)).thenReturn(Optional.of(user))
        `when`(userRepository!!.save(any(User::class.java))).thenReturn(user)

        userService!!.update(1L, UserTestFactory.initUserUpdate(email = null, password = null, proofOfWork = null, enabled = true))

        verify(userRepository!!).save(
            org.mockito.ArgumentMatchers.argThat { u: User -> u.enabled },
        )
    }

    @Test
    fun `update - should disable user when enabled is false`() {
        val user = UserTestFactory.initUser(enabled = true).copy(id = 1L)
        `when`(userRepository!!.getUserById(1L)).thenReturn(Optional.of(user))
        `when`(userRepository!!.save(any(User::class.java))).thenReturn(user)

        userService!!.update(1L, UserTestFactory.initUserUpdate(email = null, password = null, proofOfWork = null, enabled = false))

        verify(userRepository!!).save(
            org.mockito.ArgumentMatchers.argThat { u: User -> !u.enabled },
        )
    }

    @Test
    fun `update - should not change enabled when enabled is null`() {
        val user = UserTestFactory.initUser(enabled = true).copy(id = 1L)
        `when`(userRepository!!.getUserById(1L)).thenReturn(Optional.of(user))
        `when`(userRepository!!.save(any(User::class.java))).thenReturn(user)

        val result =
            userService!!.update(
                1L,
                UserTestFactory.initUserUpdate(email = null, password = null, proofOfWork = null, enabled = null),
            )

        assertThat(result.enabled).isTrue()
        verify(userRepository!!).save(
            org.mockito.ArgumentMatchers.argThat { u: User -> u.enabled },
        )
    }
}

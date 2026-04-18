package io.flavien.demo.api.user

import io.flavien.demo.domain.user.entity.ForgotPassword
import io.flavien.demo.domain.user.entity.User
import io.flavien.demo.domain.user.entity.UserActivation
import io.flavien.demo.domain.user.model.UserRole
import io.flavien.demo.domain.user.model.UserUpdate
import java.time.OffsetDateTime

object UserTestFactory {
    fun initUser(
        email: String = "perier@flavien.io",
        password: String = "Password123!",
        proofOfWork: String = "proofOfWork",
        passwordSalt: String = "salt",
        role: UserRole = UserRole.USER,
        enabled: Boolean = true,
        lastLogin: OffsetDateTime = OffsetDateTime.now(),
        id: Long? = null,
    ): User {
        val user =
            User(
                email = email,
                password = password,
                proofOfWork = proofOfWork,
                passwordSalt = passwordSalt,
                role = role,
                enabled = enabled,
                lastLogin = lastLogin,
            )
        if (id != null) {
            user.id = id
        }
        return user
    }

    fun initUserUpdate(
        email: String = "perier@flavien.io",
        password: String = "newPassword",
        proofOfWork: String = "proofOfWork",
        role: UserRole = UserRole.USER,
    ) = UserUpdate(email, password, proofOfWork, role)

    fun initUserActivation(
        token: String = "activationToken",
        userId: Long = 1L,
    ) = UserActivation(token, userId)

    fun initForgotPassword(
        token: String = "forgotPasswordToken",
        userId: Long = 1L,
    ) = ForgotPassword(token, userId)
}

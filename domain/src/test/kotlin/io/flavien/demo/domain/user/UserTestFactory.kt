package io.flavien.demo.domain.user

import io.flavien.demo.domain.user.entity.ForgotPassword
import io.flavien.demo.domain.user.entity.User
import io.flavien.demo.domain.user.entity.UserActivation
import io.flavien.demo.domain.user.model.UserUpdate
import java.time.OffsetDateTime
import java.util.UUID

object UserTestFactory {
    fun initUser(
        email: String = "perier@flavien.io",
        password: String = "Password123!",
        proofOfWork: String = "proofOfWork",
        passwordSalt: String = "salt",
        enabled: Boolean = true,
        lastLogin: OffsetDateTime = OffsetDateTime.now(),
        id: UUID? = null,
    ): User {
        val user =
            User(
                email = email,
                password = password,
                proofOfWork = proofOfWork,
                passwordSalt = passwordSalt,
                enabled = enabled,
                lastLogin = lastLogin,
            )
        if (id != null) {
            user.id = id
        }
        return user
    }

    fun initUserUpdate(
        email: String? = "perier@flavien.io",
        password: String? = "newPassword",
        proofOfWork: String? = "proofOfWork",
        enabled: Boolean? = null,
    ) = UserUpdate(email, password, proofOfWork, enabled)

    fun initUserActivation(
        token: String = "activationToken",
        userId: String = "00000000-0000-0000-0000-000000000001",
    ) = UserActivation(token, userId)

    fun initForgotPassword(
        token: String = "forgotPasswordToken",
        userId: String = "00000000-0000-0000-0000-000000000001",
    ) = ForgotPassword(token, userId)
}

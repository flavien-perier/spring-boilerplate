package io.flavien.demo.user

import io.flavien.demo.user.entity.ForgotPassword
import io.flavien.demo.user.entity.User
import io.flavien.demo.user.entity.UserActivation
import io.flavien.demo.user.model.UserRole
import io.flavien.demo.user.model.UserUpdate

object UserTestFactory {

    fun initUser(
        email: String = "perier@flavien.io",
        password: String = "Password123!",
        proofOfWork: String = "proofOfWork",
        passwordSalt: String = "salt",
        role: UserRole = UserRole.USER,
        enabled: Boolean = true,
        id: Long? = null,
    ): User {
        val user = User(email, password, proofOfWork, passwordSalt, role, enabled)
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

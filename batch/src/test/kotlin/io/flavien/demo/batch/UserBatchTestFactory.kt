package io.flavien.demo.batch

import io.flavien.demo.domain.user.entity.User
import io.flavien.demo.domain.user.model.UserRole
import java.time.OffsetDateTime

object UserBatchTestFactory {
    fun initUser(
        email: String = "perier@flavien.io",
        lastLogin: OffsetDateTime = OffsetDateTime.now(),
    ): User =
        User(
            email = email,
            password = "Password123!",
            proofOfWork = "proofOfWork",
            passwordSalt = "salt",
            role = UserRole.USER,
            enabled = true,
            lastLogin = lastLogin,
        )
}

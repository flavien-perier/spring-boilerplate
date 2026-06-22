package io.flavien.demo.batch

import io.flavien.demo.domain.user.entity.User
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
            enabled = true,
            lastLogin = lastLogin,
        )
}

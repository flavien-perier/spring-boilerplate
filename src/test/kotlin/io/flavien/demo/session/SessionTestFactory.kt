package io.flavien.demo.session

import io.flavien.demo.session.entity.RefreshToken
import io.flavien.demo.user.model.UserRole
import java.time.OffsetDateTime
import java.util.*

object SessionTestFactory {

    fun initRefreshToken() = RefreshToken(
        "test",
        UUID.randomUUID(),
        1,
        UserRole.USER,
        OffsetDateTime.now()
    )
}
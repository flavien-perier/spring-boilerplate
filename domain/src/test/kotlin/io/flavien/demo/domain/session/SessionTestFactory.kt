package io.flavien.demo.domain.session

import io.flavien.demo.domain.session.entity.AccessToken
import io.flavien.demo.domain.session.entity.RefreshToken
import io.flavien.demo.domain.session.model.Session
import io.flavien.demo.domain.user.model.UserRole
import java.time.OffsetDateTime
import java.util.UUID

object SessionTestFactory {
    fun initRefreshToken(
        id: String = "test",
        uuid: UUID = UUID.randomUUID(),
        userId: Long = 1,
        role: UserRole = UserRole.USER,
        creationDate: OffsetDateTime = OffsetDateTime.now(),
    ) = RefreshToken(
        id,
        uuid,
        userId,
        role,
        creationDate,
    )

    fun initAccessToken(
        id: String = "accessTokenId",
        userId: Long = 1L,
        role: UserRole = UserRole.USER,
        refreshTokenId: String = "refreshTokenId",
        creationDate: OffsetDateTime = OffsetDateTime.now(),
    ) = AccessToken(
        id,
        userId,
        role,
        refreshTokenId,
        creationDate,
    )

    fun initSession(
        refreshToken: RefreshToken? = initRefreshToken(),
        accessToken: AccessToken = initAccessToken(refreshTokenId = refreshToken?.id ?: "refreshTokenId"),
    ) = Session(
        refreshToken,
        accessToken,
    )
}

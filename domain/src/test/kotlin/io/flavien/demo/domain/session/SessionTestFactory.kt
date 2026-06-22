package io.flavien.demo.domain.session

import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.session.entity.AccessToken
import io.flavien.demo.domain.session.entity.RefreshToken
import io.flavien.demo.domain.session.model.Session
import java.time.OffsetDateTime
import java.util.UUID

object SessionTestFactory {
    fun initRefreshToken(
        id: String = "test",
        uuid: UUID = UUID.randomUUID(),
        userId: Long = 1,
        creationDate: OffsetDateTime = OffsetDateTime.now(),
    ) = RefreshToken(
        id,
        uuid,
        userId,
        creationDate,
    )

    fun initAccessToken(
        id: String = "accessTokenId",
        userId: Long = 1L,
        refreshTokenId: String = "refreshTokenId",
        creationDate: OffsetDateTime = OffsetDateTime.now(),
        permissions: Set<PermissionEnum> = emptySet(),
    ) = AccessToken(
        id,
        userId,
        refreshTokenId,
        creationDate,
        permissions,
    )

    fun initSession(
        refreshToken: RefreshToken? = initRefreshToken(),
        accessToken: AccessToken = initAccessToken(refreshTokenId = refreshToken?.id ?: "refreshTokenId"),
    ) = Session(
        refreshToken,
        accessToken,
    )
}

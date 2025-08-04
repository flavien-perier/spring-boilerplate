package io.flavien.demo.session

import io.flavien.demo.dto.LoginDto
import io.flavien.demo.dto.SessionDto
import io.flavien.demo.dto.SessionRenewalDto
import io.flavien.demo.session.entity.AccessToken
import io.flavien.demo.session.entity.RefreshToken
import io.flavien.demo.session.model.Session
import io.flavien.demo.user.model.UserRole
import java.time.OffsetDateTime
import java.util.*

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
        creationDate
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
        creationDate
    )

    fun initSession(
        refreshToken: RefreshToken? = initRefreshToken(),
        accessToken: AccessToken = initAccessToken(refreshTokenId = refreshToken?.id ?: "refreshTokenId"),
    ) = Session(
        refreshToken,
        accessToken
    )

    fun initSessionDto(
        accessTokenId: String = "accessTokenId",
        refreshTokenId: String? = "refreshTokenId",
    ) = SessionDto(accessTokenId, refreshTokenId)

    fun initLoginDto(
        email: String = "test@example.com",
        password: String = "Password123!",
        proofOfWork: String = "proofOfWork",
    ) = LoginDto(email, password, proofOfWork)

    fun initSessionRenewalDto(
        email: String = "test@example.com",
        refreshToken: String = "refreshToken",
    ) = SessionRenewalDto(email, refreshToken)
}

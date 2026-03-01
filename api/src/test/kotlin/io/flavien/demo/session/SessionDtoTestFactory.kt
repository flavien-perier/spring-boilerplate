package io.flavien.demo.session

import io.flavien.demo.dto.LoginDto
import io.flavien.demo.dto.SessionDto
import io.flavien.demo.dto.SessionRenewalDto

object SessionDtoTestFactory {

    fun initSessionDto(
        accessTokenId: String = "accessTokenId",
        refreshTokenId: String = "refreshTokenId",
    ) = SessionDto(refreshTokenId, accessTokenId)

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

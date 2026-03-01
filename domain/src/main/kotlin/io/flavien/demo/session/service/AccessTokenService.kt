package io.flavien.demo.session.service

import io.flavien.demo.core.util.RandomUtil
import io.flavien.demo.session.entity.AccessToken
import io.flavien.demo.session.entity.RefreshToken
import io.flavien.demo.session.exception.BadAccessTokenCreationException
import io.flavien.demo.session.exception.BadAccessTokenException
import io.flavien.demo.session.repository.AccessTokenRepository
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class AccessTokenService(
    private val accessTokenRepository: AccessTokenRepository,
    private val refreshTokenService: RefreshTokenService,
) {

    fun create(refreshToken: RefreshToken): AccessToken {
        var id = RandomUtil.randomString(64)
        while (accessTokenRepository.existsById(id)) {
            id = RandomUtil.randomString(64)
        }

        if (!refreshTokenService.exists(refreshToken.id)) {
            throw BadAccessTokenCreationException()
        }

        val accessToken = AccessToken(id, refreshToken.userId, refreshToken.role, refreshToken.id, OffsetDateTime.now())
        accessTokenRepository.save(accessToken)

        return accessToken
    }

    fun get(token: String): AccessToken {
        val optionalAccessToken = accessTokenRepository.findById(token)

        if (optionalAccessToken.isEmpty) {
            throw BadAccessTokenException()
        }

        val accessToken = optionalAccessToken.get()

        if (!refreshTokenService.exists(accessToken.refreshTokenId)) {
            delete(token)
            throw BadAccessTokenException()
        }

        return accessToken
    }

    fun delete(token: String) = accessTokenRepository.deleteById(token)

    fun deleteByUserId(userId: Long) = accessTokenRepository.deleteByUserId(userId)
}
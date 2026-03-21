package io.flavien.demo.domain.session.service

import io.flavien.demo.domain.session.entity.AccessToken
import io.flavien.demo.domain.session.entity.RefreshToken
import io.flavien.demo.domain.session.exception.BadAccessTokenCreationException
import io.flavien.demo.domain.session.exception.BadAccessTokenException
import io.flavien.demo.domain.session.repository.AccessTokenRepository
import io.flavien.demo.utils.RandomUtil
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class AccessTokenService(
    private val accessTokenRepository: AccessTokenRepository,
    private val refreshTokenService: RefreshTokenService,
) {
    @CircuitBreaker(name = "redis")
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

    @CircuitBreaker(name = "redis", fallbackMethod = "getFallback")
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

    @CircuitBreaker(name = "redis")
    fun delete(token: String) = accessTokenRepository.deleteById(token)

    @CircuitBreaker(name = "redis")
    fun deleteByUserId(userId: Long) = accessTokenRepository.deleteByUserId(userId)

    private fun getFallback(
        token: String,
        ex: Exception,
    ): AccessToken = throw BadAccessTokenException()
}

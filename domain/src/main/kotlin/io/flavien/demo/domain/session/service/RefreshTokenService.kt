package io.flavien.demo.domain.session.service

import io.flavien.demo.domain.session.entity.RefreshToken
import io.flavien.demo.domain.session.exception.BadRefreshTokenException
import io.flavien.demo.domain.session.repository.RefreshTokenRepository
import io.flavien.demo.domain.user.model.UserRole
import io.flavien.demo.utils.RandomUtil
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    @CircuitBreaker(name = "redis")
    fun create(
        userId: Long,
        role: UserRole,
    ): RefreshToken {
        var id = RandomUtil.randomString(64)
        while (refreshTokenRepository.existsById(id)) {
            id = RandomUtil.randomString(64)
        }

        val refreshToken = RefreshToken(id, UUID.randomUUID(), userId, role, OffsetDateTime.now())
        refreshTokenRepository.save(refreshToken)

        return refreshToken
    }

    @CircuitBreaker(name = "redis")
    fun get(token: String): RefreshToken {
        val optionalRefreshToken = refreshTokenRepository.findById(token)

        if (optionalRefreshToken.isEmpty) {
            throw BadRefreshTokenException()
        }

        return optionalRefreshToken.get()
    }

    @CircuitBreaker(name = "redis")
    fun get(uuid: UUID) = refreshTokenRepository.getByUuid(uuid) ?: throw BadRefreshTokenException()

    @CircuitBreaker(name = "redis")
    fun findByUserId(userId: Long) = refreshTokenRepository.findByUserId(userId).sortedByDescending { it.creationDate }

    @CircuitBreaker(name = "redis")
    fun exists(token: String) = refreshTokenRepository.existsById(token)

    @CircuitBreaker(name = "redis")
    fun delete(token: String) = refreshTokenRepository.deleteById(token)

    @CircuitBreaker(name = "redis")
    fun delete(uuid: UUID) = refreshTokenRepository.delete(get(uuid))

    @CircuitBreaker(name = "redis")
    fun deleteByUserId(userId: Long) = refreshTokenRepository.deleteByUserId(userId)
}

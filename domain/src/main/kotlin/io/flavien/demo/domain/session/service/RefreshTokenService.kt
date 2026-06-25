package io.flavien.demo.domain.session.service

import io.flavien.demo.domain.session.entity.RefreshToken
import io.flavien.demo.domain.session.exception.BadRefreshTokenException
import io.flavien.demo.domain.session.repository.RefreshTokenRepository
import io.flavien.demo.domain.shared.util.SECURE_RANDOM
import io.flavien.demo.library.common.RandomUtil
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    fun create(userId: UUID): RefreshToken {
        val id = RandomUtil.randomString(64, SECURE_RANDOM)

        val refreshToken = RefreshToken(id, UUID.randomUUID(), userId.toString(), OffsetDateTime.now())
        refreshTokenRepository.save(refreshToken)

        return refreshToken
    }

    fun get(token: String): RefreshToken = refreshTokenRepository.findById(token).orElseThrow { BadRefreshTokenException() }

    fun get(uuid: UUID) = refreshTokenRepository.getByUuid(uuid) ?: throw BadRefreshTokenException()

    fun findByUserId(userId: UUID) = refreshTokenRepository.findByUserId(userId.toString()).sortedByDescending { it.creationDate }

    fun exists(token: String) = refreshTokenRepository.existsById(token)

    fun delete(token: String) = refreshTokenRepository.deleteById(token)

    fun delete(
        uuid: UUID,
        userId: UUID,
    ) {
        val refreshToken = get(uuid)
        if (refreshToken.userId != userId.toString()) {
            throw BadRefreshTokenException()
        }
        refreshTokenRepository.delete(refreshToken)
    }

    fun deleteByUserId(userId: UUID) = refreshTokenRepository.deleteByUserId(userId.toString())
}

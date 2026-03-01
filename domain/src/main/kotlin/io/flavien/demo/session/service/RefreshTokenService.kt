package io.flavien.demo.session.service

import io.flavien.demo.core.util.RandomUtil
import io.flavien.demo.session.entity.RefreshToken
import io.flavien.demo.session.exception.BadRefreshTokenException
import io.flavien.demo.session.repository.RefreshTokenRepository
import io.flavien.demo.user.model.UserRole
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.*

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
) {

    fun create(userId: Long, role: UserRole): RefreshToken {
        var id = RandomUtil.randomString(64)
        while (refreshTokenRepository.existsById(id)) {
            id = RandomUtil.randomString(64)
        }

        val refreshToken = RefreshToken(id, UUID.randomUUID(), userId, role, OffsetDateTime.now())
        refreshTokenRepository.save(refreshToken)

        return refreshToken
    }

    fun get(token: String): RefreshToken {
        val optionalRefreshToken = refreshTokenRepository.findById(token)

        if (optionalRefreshToken.isEmpty) {
            throw BadRefreshTokenException()
        }

        return optionalRefreshToken.get()
    }

    fun get(uuid: UUID) = refreshTokenRepository.getByUuid(uuid) ?: throw BadRefreshTokenException()

    fun findByUserId(userId: Long) = refreshTokenRepository.findByUserId(userId).sortedByDescending { it.creationDate }

    fun exists(token: String) = refreshTokenRepository.existsById(token)

    fun delete(token: String) = refreshTokenRepository.deleteById(token)

    fun delete(uuid: UUID) = refreshTokenRepository.delete(get(uuid))

    fun deleteByUserId(userId: Long) = refreshTokenRepository.deleteByUserId(userId)
}
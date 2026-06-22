package io.flavien.demo.domain.session.service

import io.flavien.demo.domain.permission.service.PermissionService
import io.flavien.demo.domain.session.entity.AccessToken
import io.flavien.demo.domain.session.entity.RefreshToken
import io.flavien.demo.domain.session.exception.BadAccessTokenException
import io.flavien.demo.domain.session.repository.AccessTokenRepository
import io.flavien.demo.domain.shared.util.SECURE_RANDOM
import io.flavien.demo.library.common.RandomUtil
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class AccessTokenService(
    private val accessTokenRepository: AccessTokenRepository,
    private val refreshTokenService: RefreshTokenService,
    private val permissionService: PermissionService,
) {
    fun create(refreshToken: RefreshToken): AccessToken {
        val id = RandomUtil.randomString(64, SECURE_RANDOM)
        val permissions = permissionService.getGrantedPermissions(refreshToken.userId)

        val accessToken = AccessToken(id, refreshToken.userId, refreshToken.id, OffsetDateTime.now(), permissions)
        accessTokenRepository.save(accessToken)

        return accessToken
    }

    fun get(token: String): AccessToken {
        val accessToken = accessTokenRepository.findById(token).orElseThrow { BadAccessTokenException() }

        if (!refreshTokenService.exists(accessToken.refreshTokenId)) {
            delete(token)
            throw BadAccessTokenException()
        }

        return accessToken
    }

    fun delete(token: String) = accessTokenRepository.deleteById(token)

    fun deleteByUserId(userId: Long) = accessTokenRepository.deleteByUserId(userId)
}

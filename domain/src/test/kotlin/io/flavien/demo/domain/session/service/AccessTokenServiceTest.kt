package io.flavien.demo.domain.session.service

import io.flavien.demo.domain.comparator.OffsetDateTimeTestComparator
import io.flavien.demo.domain.permission.service.PermissionService
import io.flavien.demo.domain.session.SessionTestFactory
import io.flavien.demo.domain.session.entity.AccessToken
import io.flavien.demo.domain.session.exception.BadAccessTokenException
import io.flavien.demo.domain.session.repository.AccessTokenRepository
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.OffsetDateTime
import java.util.Optional
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class AccessTokenServiceTest {
    @InjectMocks
    var accessTokenService: AccessTokenService? = null

    @Mock
    var accessTokenRepository: AccessTokenRepository? = null

    @Mock
    var refreshTokenService: RefreshTokenService? = null

    @Mock
    var permissionService: PermissionService? = null

    private val userId = UUID.fromString("00000000-0000-0000-0000-00000000000a")
    private val userIdStr = userId.toString()

    @Test
    fun `Should create an access token`() {
        val refreshToken = SessionTestFactory.initRefreshToken()

        val accessToken = accessTokenService!!.create(refreshToken)

        assertThat(accessToken)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .withComparatorForType(OffsetDateTimeTestComparator(), OffsetDateTime::class.java)
            .isEqualTo(
                SessionTestFactory.initAccessToken(
                    "test",
                    refreshToken.userId,
                    refreshToken.id,
                    OffsetDateTime.now(),
                ),
            )

        Mockito.verify(accessTokenRepository!!).save(any(AccessToken::class.java))
    }

    @Test
    fun `Should return an access token according to its id`() {
        val tokenId = "test-token-id"
        val refreshTokenId = "test-refresh-token-id"
        val accessToken = SessionTestFactory.initAccessToken(tokenId, userIdStr, refreshTokenId)

        Mockito
            .`when`(accessTokenRepository!!.findById(tokenId))
            .thenReturn(Optional.of(accessToken))

        Mockito
            .`when`(refreshTokenService!!.exists(refreshTokenId))
            .thenReturn(true)

        val result = accessTokenService!!.get(tokenId)

        Assertions.assertThat(result).isEqualTo(accessToken)
        Mockito.verify(accessTokenRepository!!).findById(tokenId)
        Mockito.verify(refreshTokenService!!).exists(refreshTokenId)
    }

    @Test
    fun `Should fail to return an access token according to its id (Access token does not exist)`() {
        val tokenId = "non-existent-token-id"

        Mockito
            .`when`(accessTokenRepository!!.findById(tokenId))
            .thenReturn(Optional.empty())

        assertThrows(BadAccessTokenException::class.java) {
            accessTokenService!!.get(tokenId)
        }

        Mockito.verify(accessTokenRepository!!).findById(tokenId)
        Mockito.verify(refreshTokenService!!, Mockito.never()).exists(anyString())
    }

    @Test
    fun `Should delete an access token by its id`() {
        val tokenId = "token-to-delete"

        accessTokenService!!.delete(tokenId)

        Mockito.verify(accessTokenRepository!!).deleteById(tokenId)
    }

    @Test
    fun `Should delete an access token by user id`() {
        accessTokenService!!.deleteByUserId(userId)

        Mockito.verify(accessTokenRepository!!).deleteByUserId(userIdStr)
    }
}

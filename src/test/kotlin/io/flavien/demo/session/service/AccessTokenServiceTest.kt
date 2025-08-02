package io.flavien.demo.session.service

import io.flavien.demo.session.SessionTestFactory
import io.flavien.demo.session.entity.AccessToken
import io.flavien.demo.session.exception.BadAccessTokenCreationException
import io.flavien.demo.session.exception.BadAccessTokenException
import io.flavien.demo.session.repository.AccessTokenRepository
import io.flavien.demo.testCore.comparator.OffsetDateTimeTestComparator
import io.flavien.demo.user.model.UserRole
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
import java.util.*

@ExtendWith(MockitoExtension::class)
class AccessTokenServiceTest {

    @InjectMocks
    var accessTokenService: AccessTokenService? = null

    @Mock
    var accessTokenRepository: AccessTokenRepository? = null

    @Mock
    var refreshTokenService: RefreshTokenService? = null

    @Test
    fun `Should create an access token`() {
        // Given
        val refreshToken = SessionTestFactory.initRefreshToken()

        Mockito.`when`(accessTokenRepository!!.existsById(anyString()))
            .thenReturn(true, false)

        Mockito.`when`(refreshTokenService!!.exists(refreshToken.id))
            .thenReturn(true)

        // When
        val accessToken = accessTokenService!!.create(refreshToken)

        // Then
        assertThat(accessToken).usingRecursiveComparison()
            .ignoringFields("id")
            .withComparatorForType(OffsetDateTimeTestComparator(), OffsetDateTime::class.java)
            .isEqualTo(SessionTestFactory.initAccessToken(
                "test",
                refreshToken.userId,
                refreshToken.role,
                refreshToken.id,
                OffsetDateTime.now()
            ))

        Mockito.verify(accessTokenRepository!!).save(any(AccessToken::class.java))
    }

    @Test
    fun `Should fail when creating an access token (Refresh token is invalid)`() {
        // Given
        val refreshToken = SessionTestFactory.initRefreshToken()

        Mockito.`when`(refreshTokenService!!.exists(refreshToken.id))
            .thenReturn(false)

        // When/Then
        assertThrows(BadAccessTokenCreationException::class.java) {
            accessTokenService!!.create(refreshToken)
        }

        Mockito.verify(refreshTokenService!!).exists(refreshToken.id)
        Mockito.verify(accessTokenRepository!!, Mockito.never()).save(any())
    }

    @Test
    fun `Should return an access token according to its id`() {
        // Given
        val tokenId = "test-token-id"
        val refreshTokenId = "test-refresh-token-id"
        val userId = 1L
        val accessToken = SessionTestFactory.initAccessToken(tokenId, userId, UserRole.USER, refreshTokenId)

        Mockito.`when`(accessTokenRepository!!.findById(tokenId))
            .thenReturn(Optional.of(accessToken))

        Mockito.`when`(refreshTokenService!!.exists(refreshTokenId))
            .thenReturn(true)

        // When
        val result = accessTokenService!!.get(tokenId)

        // Then
        assertThat(result).isEqualTo(accessToken)
        Mockito.verify(accessTokenRepository!!).findById(tokenId)
        Mockito.verify(refreshTokenService!!).exists(refreshTokenId)
    }

    @Test
    fun `Should fail to return an access token according to its id (Access token does not exist)`() {
        // Given
        val tokenId = "non-existent-token-id"

        Mockito.`when`(accessTokenRepository!!.findById(tokenId))
            .thenReturn(Optional.empty())

        // When/Then
        assertThrows(BadAccessTokenException::class.java) {
            accessTokenService!!.get(tokenId)
        }

        Mockito.verify(accessTokenRepository!!).findById(tokenId)
        Mockito.verify(refreshTokenService!!, Mockito.never()).exists(anyString())
    }

    @Test
    fun `Should delete an access token by its id`() {
        // Given
        val tokenId = "token-to-delete"

        // When
        accessTokenService!!.delete(tokenId)

        // Then
        Mockito.verify(accessTokenRepository!!).deleteById(tokenId)
    }

    @Test
    fun `Should delete an access token by user id`() {
        // Given
        val userId = 1L

        // When
        accessTokenService!!.deleteByUserId(userId)

        // Then
        Mockito.verify(accessTokenRepository!!).deleteByUserId(userId)
    }
}

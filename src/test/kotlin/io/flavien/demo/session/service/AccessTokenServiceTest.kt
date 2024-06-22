package io.flavien.demo.session.service

import io.flavien.demo.session.SessionTestFactory
import io.flavien.demo.session.entity.AccessToken
import io.flavien.demo.session.repository.AccessTokenRepository
import io.flavien.demo.testCore.comparator.OffsetDateTimeTestComparator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.OffsetDateTime

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
            .isEqualTo(AccessToken(
                "test",
                refreshToken.userId,
                refreshToken.role,
                refreshToken.id,
                OffsetDateTime.now()
            ))

        Mockito.verify(accessTokenRepository!!).save(any(AccessToken::class.java))
    }

    // TODO: Implement tests
    @Test
    fun `Should fail when creating an access token (Refresh token is invalid)`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should return an access token according to its id`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should fail to return an access token according to its id (Access token does not exist)`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should delete an access token by its id`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should delete an access token by user id`() {
        // Given

        // When

        // Then
    }
}
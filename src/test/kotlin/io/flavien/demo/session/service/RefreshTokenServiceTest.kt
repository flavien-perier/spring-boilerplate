package io.flavien.demo.session.service

import io.flavien.demo.session.SessionTestFactory
import io.flavien.demo.session.entity.RefreshToken
import io.flavien.demo.session.exception.BadRefreshTokenException
import io.flavien.demo.session.repository.RefreshTokenRepository
import io.flavien.demo.user.model.UserRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.OffsetDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class RefreshTokenServiceTest {

    @InjectMocks
    var refreshTokenService: RefreshTokenService? = null

    @Mock
    var refreshTokenRepository: RefreshTokenRepository? = null

    @Test
    fun `Should create an refresh token`() {
        // Given
        val userId = 1L
        val role = UserRole.USER

        // When
        val result = refreshTokenService!!.create(userId, role)

        // Then
        verify(refreshTokenRepository!!).existsById(result.id)

        val refreshTokenCaptor = org.mockito.ArgumentCaptor.forClass(RefreshToken::class.java)
        verify(refreshTokenRepository!!).save(refreshTokenCaptor.capture())
        val savedRefreshToken = refreshTokenCaptor.value

        assertEquals(userId, savedRefreshToken.userId)
        assertEquals(role, savedRefreshToken.role)

        assertEquals(userId, result.userId)
        assertEquals(role, result.role)
    }

    @Test
    fun `Should return an refresh token according to its id`() {
        // Given
        val tokenId = "refreshTokenId"
        val refreshToken = SessionTestFactory.initRefreshToken()

        `when`(refreshTokenRepository!!.findById(tokenId)).thenReturn(Optional.of(refreshToken))

        // When
        val result = refreshTokenService!!.get(tokenId)

        // Then
        verify(refreshTokenRepository!!).findById(tokenId)
        assertEquals(refreshToken, result)
    }

    @Test
    fun `Should fail to return an refresh token according to its id (Refresh token does not exist)`() {
        // Given
        val tokenId = "nonExistentTokenId"

        `when`(refreshTokenRepository!!.findById(tokenId)).thenReturn(Optional.empty())

        // When & Then
        assertThrows(BadRefreshTokenException::class.java) {
            refreshTokenService!!.get(tokenId)
        }

        verify(refreshTokenRepository!!).findById(tokenId)
    }

    @Test
    fun `Should return an refresh token according to its uuid`() {
        // Given
        val refreshToken = SessionTestFactory.initRefreshToken()
        val uuid = refreshToken.uuid

        `when`(refreshTokenRepository!!.getByUuid(uuid)).thenReturn(refreshToken)

        // When
        val result = refreshTokenService!!.get(uuid)

        // Then
        verify(refreshTokenRepository!!).getByUuid(uuid)
        assertEquals(refreshToken, result)
    }

    @Test
    fun `Should fail to return an refresh token according to its uuid (Refresh token does not exist)`() {
        // Given
        val uuid = UUID.randomUUID()

        `when`(refreshTokenRepository!!.getByUuid(uuid)).thenReturn(null)

        // When & Then
        assertThrows(BadRefreshTokenException::class.java) {
            refreshTokenService!!.get(uuid)
        }

        verify(refreshTokenRepository!!).getByUuid(uuid)
    }

    @Test
    fun `Should return a list of refresh tokens based on an userId`() {
        // Given
        val userId = 1L
        val refreshToken1 = SessionTestFactory.initRefreshToken()
        val refreshToken2 = SessionTestFactory.initRefreshToken(
            id = "test2",
            userId = userId,
            creationDate = OffsetDateTime.now().minusHours(1)
        )
        val refreshTokens = listOf(refreshToken1, refreshToken2)

        `when`(refreshTokenRepository!!.findByUserId(userId)).thenReturn(refreshTokens)

        // When
        val result = refreshTokenService!!.findByUserId(userId)

        // Then
        verify(refreshTokenRepository!!).findByUserId(userId)

        assertEquals(2, result.size)
        assertEquals(refreshToken1, result[0])
        assertEquals(refreshToken2, result[1])
    }

    @Test
    fun `Should return an empty list of refresh tokens based on an userId (No userId matches)`() {
        // Given
        val userId = 999L

        `when`(refreshTokenRepository!!.findByUserId(userId)).thenReturn(emptyList())

        // When
        val result = refreshTokenService!!.findByUserId(userId)

        // Then
        verify(refreshTokenRepository!!).findByUserId(userId)
        assertEquals(0, result.size)
    }

    @Test
    fun `Should return that the refresh token exists according to its id`() {
        // Given
        val tokenId = "existing-token-id"

        `when`(refreshTokenRepository!!.existsById(tokenId)).thenReturn(true)

        // When
        val result = refreshTokenService!!.exists(tokenId)

        // Then
        verify(refreshTokenRepository!!).existsById(tokenId)
        assertEquals(true, result)
    }

    @Test
    fun `Should return that the refresh token does not exist according to its id`() {
        // Given
        val tokenId = "non-existent-token-id"

        `when`(refreshTokenRepository!!.existsById(tokenId)).thenReturn(false)

        // When
        val result = refreshTokenService!!.exists(tokenId)

        // Then
        verify(refreshTokenRepository!!).existsById(tokenId)
        assertEquals(false, result)
    }

    @Test
    fun `Should delete a refresh token according to its id`() {
        // Given
        val tokenId = "token-to-delete"

        // When
        refreshTokenService!!.delete(tokenId)

        // Then
        verify(refreshTokenRepository!!).deleteById(tokenId)
    }

    @Test
    fun `Should delete a refresh token according to its uuid`() {
        // Given
        val uuid = UUID.randomUUID()
        val refreshToken = SessionTestFactory.initRefreshToken()

        `when`(refreshTokenRepository!!.getByUuid(uuid)).thenReturn(refreshToken)

        // When
        refreshTokenService!!.delete(uuid)

        // Then
        verify(refreshTokenRepository!!).getByUuid(uuid)
        verify(refreshTokenRepository!!).delete(refreshToken)
    }

    @Test
    fun `Should delete a refresh token according to its userId`() {
        // Given
        val userId = 1L

        // When
        refreshTokenService!!.deleteByUserId(userId)

        // Then
        verify(refreshTokenRepository!!).deleteByUserId(userId)
    }
}

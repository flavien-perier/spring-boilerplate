package io.flavien.demo.domain.session.service

import io.flavien.demo.domain.session.SessionTestFactory
import io.flavien.demo.domain.session.entity.RefreshToken
import io.flavien.demo.domain.session.exception.BadRefreshTokenException
import io.flavien.demo.domain.session.repository.RefreshTokenRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.OffsetDateTime
import java.util.Optional
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class RefreshTokenServiceTest {
    @InjectMocks
    var refreshTokenService: RefreshTokenService? = null

    @Mock
    var refreshTokenRepository: RefreshTokenRepository? = null

    private val userId = UUID.fromString("00000000-0000-0000-0000-00000000000a")
    private val userIdStr = userId.toString()

    @Test
    fun `Should create an refresh token`() {
        val result = refreshTokenService!!.create(userId)

        val refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken::class.java)
        verify(refreshTokenRepository!!).save(refreshTokenCaptor.capture())
        val savedRefreshToken = refreshTokenCaptor.value

        assertEquals(userIdStr, savedRefreshToken.userId)
        assertEquals(userIdStr, result.userId)
    }

    @Test
    fun `Should return an refresh token according to its id`() {
        val tokenId = "refreshTokenId"
        val refreshToken = SessionTestFactory.initRefreshToken()

        `when`(refreshTokenRepository!!.findById(tokenId)).thenReturn(Optional.of(refreshToken))

        val result = refreshTokenService!!.get(tokenId)

        verify(refreshTokenRepository!!).findById(tokenId)
        assertEquals(refreshToken, result)
    }

    @Test
    fun `Should fail to return an refresh token according to its id (Refresh token does not exist)`() {
        val tokenId = "nonExistentTokenId"

        `when`(refreshTokenRepository!!.findById(tokenId)).thenReturn(Optional.empty())

        assertThrows(BadRefreshTokenException::class.java) {
            refreshTokenService!!.get(tokenId)
        }

        verify(refreshTokenRepository!!).findById(tokenId)
    }

    @Test
    fun `Should return an refresh token according to its uuid`() {
        val refreshToken = SessionTestFactory.initRefreshToken()
        val uuid = refreshToken.uuid

        `when`(refreshTokenRepository!!.getByUuid(uuid)).thenReturn(refreshToken)

        val result = refreshTokenService!!.get(uuid)

        verify(refreshTokenRepository!!).getByUuid(uuid)
        assertEquals(refreshToken, result)
    }

    @Test
    fun `Should fail to return an refresh token according to its uuid (Refresh token does not exist)`() {
        val uuid = UUID.randomUUID()

        `when`(refreshTokenRepository!!.getByUuid(uuid)).thenReturn(null)

        assertThrows(BadRefreshTokenException::class.java) {
            refreshTokenService!!.get(uuid)
        }

        verify(refreshTokenRepository!!).getByUuid(uuid)
    }

    @Test
    fun `Should return a list of refresh tokens based on an userId`() {
        val refreshToken1 = SessionTestFactory.initRefreshToken()
        val refreshToken2 =
            SessionTestFactory.initRefreshToken(
                id = "test2",
                userId = userIdStr,
                creationDate = OffsetDateTime.now().minusHours(1),
            )
        val refreshTokens = listOf(refreshToken1, refreshToken2)

        `when`(refreshTokenRepository!!.findByUserId(userIdStr)).thenReturn(refreshTokens)

        val result = refreshTokenService!!.findByUserId(userId)

        verify(refreshTokenRepository!!).findByUserId(userIdStr)
        assertEquals(2, result.size)
        assertEquals(refreshToken1, result[0])
        assertEquals(refreshToken2, result[1])
    }

    @Test
    fun `Should return an empty list of refresh tokens based on an userId (No userId matches)`() {
        val unknownUserIdStr = "00000000-0000-0000-0000-000000000999"

        `when`(refreshTokenRepository!!.findByUserId(unknownUserIdStr)).thenReturn(emptyList())

        val result = refreshTokenService!!.findByUserId(UUID.fromString(unknownUserIdStr))

        verify(refreshTokenRepository!!).findByUserId(unknownUserIdStr)
        assertEquals(0, result.size)
    }

    @Test
    fun `Should return that the refresh token exists according to its id`() {
        val tokenId = "existing-token-id"

        `when`(refreshTokenRepository!!.existsById(tokenId)).thenReturn(true)

        val result = refreshTokenService!!.exists(tokenId)

        verify(refreshTokenRepository!!).existsById(tokenId)
        assertEquals(true, result)
    }

    @Test
    fun `Should return that the refresh token does not exist according to its id`() {
        val tokenId = "non-existent-token-id"

        `when`(refreshTokenRepository!!.existsById(tokenId)).thenReturn(false)

        val result = refreshTokenService!!.exists(tokenId)

        verify(refreshTokenRepository!!).existsById(tokenId)
        assertEquals(false, result)
    }

    @Test
    fun `Should delete a refresh token according to its id`() {
        val tokenId = "token-to-delete"

        refreshTokenService!!.delete(tokenId)

        verify(refreshTokenRepository!!).deleteById(tokenId)
    }

    @Test
    fun `Should delete a refresh token according to its uuid`() {
        val uuid = UUID.randomUUID()
        val refreshToken = SessionTestFactory.initRefreshToken(userId = userIdStr)

        `when`(refreshTokenRepository!!.getByUuid(uuid)).thenReturn(refreshToken)

        refreshTokenService!!.delete(uuid, userId)

        verify(refreshTokenRepository!!).getByUuid(uuid)
        verify(refreshTokenRepository!!).delete(refreshToken)
    }

    @Test
    fun `Should throw a BadRefreshTokenException when deleting a refresh token owned by another user`() {
        val uuid = UUID.randomUUID()
        val refreshToken = SessionTestFactory.initRefreshToken(userId = userIdStr)
        val otherUserId = UUID.fromString("00000000-0000-0000-0000-000000000002")

        `when`(refreshTokenRepository!!.getByUuid(uuid)).thenReturn(refreshToken)

        assertThrows(BadRefreshTokenException::class.java) {
            refreshTokenService!!.delete(uuid, otherUserId)
        }

        verify(refreshTokenRepository!!).getByUuid(uuid)
        verify(refreshTokenRepository!!, never()).delete(refreshToken)
    }

    @Test
    fun `Should delete a refresh token according to its userId`() {
        refreshTokenService!!.deleteByUserId(userId)

        verify(refreshTokenRepository!!).deleteByUserId(userIdStr)
    }
}

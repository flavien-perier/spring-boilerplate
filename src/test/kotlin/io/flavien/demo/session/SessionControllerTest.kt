package io.flavien.demo.session

import io.flavien.demo.dto.RefreshTokenPropertiesDto
import io.flavien.demo.session.mapper.RefreshTokenMapper
import io.flavien.demo.session.mapper.SessionMapper
import io.flavien.demo.session.service.RefreshTokenService
import io.flavien.demo.session.service.SessionService
import io.flavien.demo.session.util.ContextUtil
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import java.util.*

@ExtendWith(MockitoExtension::class)
class SessionControllerTest {

    @InjectMocks
    var sessionController: SessionController? = null

    @Mock
    var sessionService: SessionService? = null

    @Mock
    var refreshTokenService: RefreshTokenService? = null

    @Mock
    var sessionMapper: SessionMapper? = null

    @Mock
    var refreshTokenMapper: RefreshTokenMapper? = null

    @Test
    fun `Test login`() {
        // Given
        val email = "test@example.com"
        val password = "Password123!"
        val proofOfWork = "proofOfWork"
        val loginDto = SessionTestFactory.initLoginDto(email, password, proofOfWork)

        val refreshToken = SessionTestFactory.initRefreshToken()
        val accessToken = SessionTestFactory.initAccessToken()
        val session = SessionTestFactory.initSession(refreshToken, accessToken)
        val sessionDto = SessionTestFactory.initSessionDto()

        `when`(sessionService!!.login(email, password, proofOfWork)).thenReturn(session)
        `when`(sessionMapper!!.toSessionDto(session)).thenReturn(sessionDto)

        // When
        val response = sessionController!!.login(loginDto)

        // Then
        verify(sessionService!!).login(email, password, proofOfWork)
        verify(sessionMapper!!).toSessionDto(session)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(sessionDto, response.body)
    }

    @Test
    fun `Test logout`() {
        // Given
        val refreshTokenId = "refreshTokenId"

        // Mock ContextUtil.refreshTokenId
        mockkObject(ContextUtil)
        every { ContextUtil.refreshTokenId } returns refreshTokenId

        // When
        val response = sessionController!!.logout()

        // Then
        verify(refreshTokenService!!).delete(refreshTokenId)
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        // Clean up
        unmockkObject(ContextUtil)
    }

    @Test
    fun `Test findSessions`() {
        // Given
        val userId = 1L
        val refreshTokens = listOf(SessionTestFactory.initRefreshToken())
        val refreshTokenPropertiesDtos = listOf(
            RefreshTokenPropertiesDto(refreshTokens[0].uuid.toString(), refreshTokens[0].creationDate)
        )

        mockkObject(ContextUtil)
        every { ContextUtil.userId } returns userId

        `when`(refreshTokenService!!.findByUserId(userId)).thenReturn(refreshTokens)
        `when`(refreshTokenMapper!!.toRefreshTokenPropertiesDtoList(refreshTokens)).thenReturn(refreshTokenPropertiesDtos)

        // When
        val response = sessionController!!.findSessions()

        // Then
        verify(refreshTokenService!!).findByUserId(userId)
        verify(refreshTokenMapper!!).toRefreshTokenPropertiesDtoList(refreshTokens)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(refreshTokenPropertiesDtos, response.body)

        // Clean up
        unmockkObject(ContextUtil)
    }

    @Test
    fun `Test renewSession`() {
        // Given
        val email = "test@example.com"
        val refreshToken = "refreshToken"
        val sessionRenewalDto = SessionTestFactory.initSessionRenewalDto(email, refreshToken)

        val accessToken = SessionTestFactory.initAccessToken()
        val session = SessionTestFactory.initSession(null, accessToken)
        val sessionDto = SessionTestFactory.initSessionDto(accessToken.id, null)

        `when`(sessionService!!.renew(email, refreshToken)).thenReturn(session)
        `when`(sessionMapper!!.toSessionDto(session)).thenReturn(sessionDto)

        // When
        val response = sessionController!!.renewSession(sessionRenewalDto)

        // Then
        verify(sessionService!!).renew(email, refreshToken)
        verify(sessionMapper!!).toSessionDto(session)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(sessionDto, response.body)
    }

    @Test
    fun `Test deleteSession`() {
        // Given
        val sessionUuid = "123e4567-e89b-12d3-a456-426614174000"
        val uuid = UUID.fromString(sessionUuid)

        // When
        val response = sessionController!!.deleteSession(sessionUuid)

        // Then
        verify(refreshTokenService!!).delete(uuid)
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }
}

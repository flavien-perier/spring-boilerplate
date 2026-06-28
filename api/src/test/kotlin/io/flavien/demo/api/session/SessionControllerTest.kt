package io.flavien.demo.api.session

import io.flavien.demo.api.generated.dto.RefreshTokenPropertiesDto
import io.flavien.demo.api.generated.dto.RefreshTokenPropertiesPageDto
import io.flavien.demo.api.session.mapper.RefreshTokenMapper
import io.flavien.demo.api.session.mapper.SessionMapper
import io.flavien.demo.api.session.util.ContextUtil
import io.flavien.demo.domain.session.service.RefreshTokenService
import io.flavien.demo.domain.session.service.SessionService
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class SessionControllerTest {
    private lateinit var sessionController: SessionController

    @Mock
    var sessionService: SessionService? = null

    @Mock
    var refreshTokenService: RefreshTokenService? = null

    @Mock
    var sessionMapper: SessionMapper? = null

    @Mock
    var refreshTokenMapper: RefreshTokenMapper? = null

    companion object {
        private val USER_ID = UUID.fromString("00000000-0000-7000-8000-000000000001")
    }

    @BeforeEach
    fun setUp() {
        sessionController =
            SessionController(
                sessionService!!,
                refreshTokenService!!,
                sessionMapper!!,
                refreshTokenMapper!!,
                true,
            )
    }

    @Test
    fun `Test login`() {
        // Given
        val email = "test@example.com"
        val password = "Password123!"
        val proofOfWork = "proofOfWork"
        val loginDto = SessionDtoTestFactory.initLoginDto(email, password, proofOfWork)
        val refreshToken = SessionTestFactory.initRefreshToken()
        val accessToken = SessionTestFactory.initAccessToken()
        val session = SessionTestFactory.initSession(refreshToken, accessToken)
        val sessionDto = SessionDtoTestFactory.initSessionDto()

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

        mockkObject(ContextUtil)
        every { ContextUtil.refreshTokenId } returns refreshTokenId

        // When
        val response = sessionController!!.logout()

        // Then
        verify(refreshTokenService!!).delete(refreshTokenId)
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        unmockkObject(ContextUtil)
    }

    @Test
    fun `Test findSessions`() {
        // Given
        val userId = USER_ID
        val page = 1
        val pageSize = 10
        val pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "creationDate"))
        val refreshTokens = listOf(SessionTestFactory.initRefreshToken())
        val refreshTokenPropertiesDtos =
            listOf(
                RefreshTokenPropertiesDto(refreshTokens[0].uuid.toString(), refreshTokens[0].creationDate),
            )
        val refreshTokensPage = PageImpl(refreshTokens, pageable, 1)
        val expectedPageDto =
            RefreshTokenPropertiesPageDto(
                totalElements = 1,
                totalPages = 1,
                number = 0,
                propertySize = pageSize,
                content = refreshTokenPropertiesDtos,
            )

        mockkObject(ContextUtil)
        every { ContextUtil.userId } returns userId

        `when`(refreshTokenService!!.findByUserId(userId, pageable)).thenReturn(refreshTokensPage)
        `when`(refreshTokenMapper!!.toRefreshTokenPropertiesPageDto(refreshTokensPage)).thenReturn(expectedPageDto)

        // When
        val response = sessionController!!.findSessions(page, pageSize)

        // Then
        verify(refreshTokenService!!).findByUserId(userId, pageable)
        verify(refreshTokenMapper!!).toRefreshTokenPropertiesPageDto(refreshTokensPage)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedPageDto, response.body)
        assertEquals(pageSize, response.body!!.propertySize)
        assertEquals(1L, response.body!!.totalElements)
        assertEquals(1, response.body!!.totalPages)

        unmockkObject(ContextUtil)
    }

    @Test
    fun `Test renewSession`() {
        // Given
        val email = "test@example.com"
        val refreshToken = "refreshToken"
        val sessionRenewalDto = SessionDtoTestFactory.initSessionRenewalDto(email, refreshToken)

        val accessToken = SessionTestFactory.initAccessToken()
        val session = SessionTestFactory.initSession(null, accessToken)
        val sessionDto = SessionDtoTestFactory.initSessionDto(accessToken.id, "")

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
        val userId = USER_ID

        mockkObject(ContextUtil)
        every { ContextUtil.userId } returns userId

        // When
        val response = sessionController!!.deleteSession(sessionUuid)

        // Then
        verify(refreshTokenService!!).delete(uuid, userId)
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        unmockkObject(ContextUtil)
    }
}

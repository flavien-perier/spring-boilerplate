package io.flavien.demo.session

import io.flavien.demo.api.SessionApi
import io.flavien.demo.dto.*
import io.flavien.demo.session.mapper.RefreshTokenMapper
import io.flavien.demo.session.mapper.SessionMapper
import io.flavien.demo.session.service.RefreshTokenService
import io.flavien.demo.session.service.SessionService
import io.flavien.demo.session.util.ContextUtil
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import java.time.Duration
import java.util.*

@Controller
class SessionController(
    private val sessionService: SessionService,
    private val refreshTokenService: RefreshTokenService,
    private val sessionMapper: SessionMapper,
    private val refreshTokenMapper: RefreshTokenMapper,
) : SessionApi {

    override fun login(loginDto: LoginDto): ResponseEntity<SessionDto> {
        val session = sessionService.login(loginDto.email, loginDto.password, loginDto.proofOfWork)
        return ResponseEntity(
            sessionMapper.toSessionDto(session),
            HttpStatus.OK,
        )
    }

    override fun loginWeb(loginDto: LoginDto): ResponseEntity<SessionWebDto> {
        val session = sessionService.login(loginDto.email, loginDto.password, loginDto.proofOfWork)

        val refreshToken = session.refreshToken?.id ?: ""

        val refreshTokenCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/api/session/renew")
            .maxAge(Duration.ofDays(REFRESH_TOKEN_DURATION_DAYS))
            .sameSite("Strict")
            .build()

        val emailCookie = ResponseCookie.from(EMAIL_COOKIE_NAME, loginDto.email)
            .httpOnly(false)
            .secure(false)
            .path("/")
            .maxAge(Duration.ofDays(REFRESH_TOKEN_DURATION_DAYS))
            .sameSite("Strict")
            .build()

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .header(HttpHeaders.SET_COOKIE, emailCookie.toString())
            .body(sessionMapper.toSessionWebDto(session))
    }

    override fun logout(): ResponseEntity<Void> {
        refreshTokenService.delete(ContextUtil.refreshTokenId)

        val clearedRefreshCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)
            .build()

        return ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, clearedRefreshCookie.toString())
            .build()
    }

    override fun findSessions(): ResponseEntity<List<RefreshTokenPropertiesDto>> {
        val refreshTokens = refreshTokenService.findByUserId(ContextUtil.userId)
        return ResponseEntity(
            refreshTokenMapper.toRefreshTokenPropertiesDtoList(refreshTokens),
            HttpStatus.OK,
        )
    }

    override fun renewSession(sessionRenewalDto: SessionRenewalDto): ResponseEntity<SessionDto> {
        val session = sessionService.renew(sessionRenewalDto.email, sessionRenewalDto.refreshToken)
        return ResponseEntity(
            sessionMapper.toSessionDto(session),
            HttpStatus.OK,
        )
    }

    override fun renewSessionWeb(refreshToken: String, sessionRenewalWebDto: SessionRenewalWebDto): ResponseEntity<SessionWebDto> {
        val session = sessionService.renew(sessionRenewalWebDto.email, refreshToken)
        return ResponseEntity(
            sessionMapper.toSessionWebDto(session),
            HttpStatus.OK,
        )
    }

    override fun deleteSession(sessionUuid: String): ResponseEntity<Void> {
        refreshTokenService.delete(UUID.fromString(sessionUuid))
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    companion object {
        private const val EMAIL_COOKIE_NAME = "email"
        private const val REFRESH_TOKEN_COOKIE_NAME = "refresh_token"
        private const val REFRESH_TOKEN_DURATION_DAYS = 30L
    }
}
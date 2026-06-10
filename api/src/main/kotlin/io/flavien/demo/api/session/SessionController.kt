package io.flavien.demo.api.session

import io.flavien.demo.api.generated.api.SessionApi
import io.flavien.demo.api.generated.dto.LoginDto
import io.flavien.demo.api.generated.dto.RefreshTokenPropertiesDto
import io.flavien.demo.api.generated.dto.SessionDto
import io.flavien.demo.api.generated.dto.SessionRenewalDto
import io.flavien.demo.api.generated.dto.SessionRenewalWebDto
import io.flavien.demo.api.generated.dto.SessionWebDto
import io.flavien.demo.api.session.mapper.RefreshTokenMapper
import io.flavien.demo.api.session.mapper.SessionMapper
import io.flavien.demo.api.session.util.ContextUtil
import io.flavien.demo.domain.session.exception.BadRefreshTokenException
import io.flavien.demo.domain.session.model.REFRESH_TOKEN_TTL_SECONDS
import io.flavien.demo.domain.session.service.RefreshTokenService
import io.flavien.demo.domain.session.service.SessionService
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import java.time.Duration
import java.util.UUID

@Controller
class SessionController(
    private val sessionService: SessionService,
    private val refreshTokenService: RefreshTokenService,
    private val sessionMapper: SessionMapper,
    private val refreshTokenMapper: RefreshTokenMapper,
) : SessionApi {
    override fun login(loginDto: LoginDto): ResponseEntity<SessionDto> {
        val session = sessionService.login(loginDto.email, loginDto.password, loginDto.proofOfWork, loginDto.otp)
        return ResponseEntity.ok(sessionMapper.toSessionDto(session))
    }

    override fun loginWeb(loginDto: LoginDto): ResponseEntity<SessionWebDto> {
        val session = sessionService.login(loginDto.email, loginDto.password, loginDto.proofOfWork, loginDto.otp)

        val refreshToken = checkNotNull(session.refreshToken) { "Login must return a refresh token" }.id

        val refreshTokenCookie =
            ResponseCookie
                .from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/api/session/renew")
                .maxAge(Duration.ofSeconds(REFRESH_TOKEN_TTL_SECONDS))
                .sameSite("Strict")
                .build()

        val emailCookie =
            ResponseCookie
                .from(EMAIL_COOKIE_NAME, loginDto.email)
                .httpOnly(false)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofSeconds(REFRESH_TOKEN_TTL_SECONDS))
                .sameSite("Strict")
                .build()

        return ResponseEntity
            .ok()
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .header(HttpHeaders.SET_COOKIE, emailCookie.toString())
            .body(sessionMapper.toSessionWebDto(session))
    }

    override fun logout(): ResponseEntity<Unit> {
        refreshTokenService.delete(ContextUtil.refreshTokenId)

        val clearedRefreshCookie =
            ResponseCookie
                .from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .path("/api/session/renew")
                .sameSite("Strict")
                .maxAge(0)
                .build()

        val clearedEmailCookie =
            ResponseCookie
                .from(EMAIL_COOKIE_NAME, "")
                .httpOnly(false)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(0)
                .build()

        return ResponseEntity
            .noContent()
            .header(HttpHeaders.SET_COOKIE, clearedRefreshCookie.toString())
            .header(HttpHeaders.SET_COOKIE, clearedEmailCookie.toString())
            .build()
    }

    override fun findSessions(): ResponseEntity<List<RefreshTokenPropertiesDto>> {
        val refreshTokens = refreshTokenService.findByUserId(ContextUtil.userId)
        return ResponseEntity.ok(refreshTokenMapper.toRefreshTokenPropertiesDtoList(refreshTokens))
    }

    override fun renewSession(sessionRenewalDto: SessionRenewalDto): ResponseEntity<SessionDto> {
        val session = sessionService.renew(sessionRenewalDto.email, sessionRenewalDto.refreshToken)
        return ResponseEntity.ok(sessionMapper.toSessionDto(session))
    }

    override fun renewSessionWeb(
        refreshToken: String,
        sessionRenewalWebDto: SessionRenewalWebDto,
    ): ResponseEntity<SessionWebDto> {
        val session = sessionService.renew(sessionRenewalWebDto.email, refreshToken)
        return ResponseEntity.ok(sessionMapper.toSessionWebDto(session))
    }

    override fun deleteSession(sessionUuid: String): ResponseEntity<Unit> {
        val uuid = runCatching { UUID.fromString(sessionUuid) }.getOrElse { throw BadRefreshTokenException() }
        refreshTokenService.delete(uuid, ContextUtil.userId)
        return ResponseEntity.noContent().build()
    }

    companion object {
        private const val EMAIL_COOKIE_NAME = "email"
        private const val REFRESH_TOKEN_COOKIE_NAME = "refresh_token"
    }
}

package io.flavien.demo.session

import io.flavien.demo.api.SessionApi
import io.flavien.demo.dto.LoginDto
import io.flavien.demo.dto.RefreshTokenPropertiesDto
import io.flavien.demo.dto.SessionDto
import io.flavien.demo.dto.SessionRenewalDto
import io.flavien.demo.session.mapper.RefreshTokenMapper
import io.flavien.demo.session.mapper.SessionMapper
import io.flavien.demo.session.service.RefreshTokenService
import io.flavien.demo.session.service.SessionService
import io.flavien.demo.session.util.ContextUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
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

    override fun logout(): ResponseEntity<Void> {
        refreshTokenService.delete(ContextUtil.refreshTokenId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
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

    override fun deleteSession(sessionUuid: String): ResponseEntity<Void> {
        refreshTokenService.delete(UUID.fromString(sessionUuid))
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
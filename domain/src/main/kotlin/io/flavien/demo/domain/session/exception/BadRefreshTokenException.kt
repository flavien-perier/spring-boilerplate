package io.flavien.demo.domain.session.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class BadRefreshTokenException :
    FioException("Bad refresh token", HttpStatus.UNAUTHORIZED, "BAD_REFRESH_TOKEN")

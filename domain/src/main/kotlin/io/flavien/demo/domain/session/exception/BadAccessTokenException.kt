package io.flavien.demo.domain.session.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class BadAccessTokenException :
    FioException("Bad access token", HttpStatus.UNAUTHORIZED, "BAD_ACCESS_TOKEN")

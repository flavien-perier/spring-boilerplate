package io.flavien.demo.domain.session.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class OtpRequiredException :
    FioException("OTP required", HttpStatus.UNAUTHORIZED, "OTP_REQUIRED")

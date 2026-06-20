package io.flavien.demo.domain.session.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class OtpAlreadyConfiguredException :
    FioException("OTP already configured", HttpStatus.CONFLICT, "OTP_ALREADY_CONFIGURED")

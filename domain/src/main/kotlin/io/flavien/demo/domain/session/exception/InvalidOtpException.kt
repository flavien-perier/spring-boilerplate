package io.flavien.demo.domain.session.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class InvalidOtpException :
    FioException("Invalid OTP", HttpStatus.BAD_REQUEST, "INVALID_OTP")

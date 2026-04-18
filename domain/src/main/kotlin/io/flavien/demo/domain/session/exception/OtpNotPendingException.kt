package io.flavien.demo.domain.session.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class OtpNotPendingException : RuntimeException("No OTP setup pending")

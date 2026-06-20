package io.flavien.demo.domain.session.exception

import io.flavien.demo.domain.shared.exception.FioException
import org.springframework.http.HttpStatus

class ContextException :
    FioException("Context exception", HttpStatus.INTERNAL_SERVER_ERROR, "CONTEXT_EXCEPTION")

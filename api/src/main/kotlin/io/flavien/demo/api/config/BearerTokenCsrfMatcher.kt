package io.flavien.demo.api.config

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.security.web.csrf.CsrfFilter
import org.springframework.security.web.util.matcher.RequestMatcher

class BearerTokenCsrfMatcher : RequestMatcher {
    private val delegate = CsrfFilter.DEFAULT_CSRF_MATCHER

    override fun matches(request: HttpServletRequest): Boolean {
        if (!delegate.matches(request)) return false
        val auth = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return true
        return !auth.startsWith("Bearer ")
    }
}

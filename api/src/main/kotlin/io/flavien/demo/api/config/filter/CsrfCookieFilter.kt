package io.flavien.demo.api.config.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.security.web.csrf.DeferredCsrfToken
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class CsrfCookieFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
    ) {
        val deferredCsrfToken = request.getAttribute(CsrfToken::class.java.name)
        if (deferredCsrfToken is DeferredCsrfToken) {
            deferredCsrfToken.get()
        }
        chain.doFilter(request, response)
    }
}

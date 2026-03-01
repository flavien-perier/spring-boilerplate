package io.flavien.demo.session.filter

import io.flavien.demo.session.exception.AuthenticationFailedException
import io.flavien.demo.session.service.AccessTokenService
import io.flavien.demo.session.util.ContextUtil
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.PathItem
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.time.Instant

@Component
class SessionAuthenticationFilter(
    private val openAPI: OpenAPI,
    private val accessTokenService: AccessTokenService,
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val requestURI = httpServletRequest.requestURI
            val method = httpServletRequest.method

            if (requestURI.startsWith("/api/")) {
                testApiSession(requestURI, method, httpServletRequest)
            }
            filterChain.doFilter(httpServletRequest, httpServletResponse)
        } catch (e: Exception) {
            logger.warn("Authentication failed : ${e.message}")

            // Waits a certain amount of time to avoid time-based discovery attacks
            val now = Instant.now().toEpochMilli()
            val wait = now - ((now / 100) * 100)
            Thread.sleep(wait)
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
            return
        }
    }

    private fun testApiSession(
        requestURI: String,
        method: String,
        httpServletRequest: HttpServletRequest,
    ) {
        val path = openAPI.paths.toList()
            .firstOrNull { testUri(it.first, requestURI) }
            ?.second ?: throw AuthenticationFailedException()

        val securityRequirements = getOperation(path, method)
            .security?.flatMap { it.entries }

        if (securityRequirements.isNullOrEmpty()) {
            // Authentication is not required for this request
            return
        }

        val authorizationHeader = httpServletRequest.getHeader("Authorization")
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // The bearer token is badly formatted or non-existent
            throw AuthenticationFailedException()
        }

        val bearer = authorizationHeader.replace("Bearer ", "")
        val accessToken = accessTokenService.get(bearer)

        val hasRole = securityRequirements
            .filter { it.key == "bearer" }
            .flatMap { it.value }
            .contains(accessToken.role.name.lowercase())

        if (!hasRole) {
            // The user does not have the right role to access this resource
            throw AuthenticationFailedException()
        }

        // Authentication is OK
        ContextUtil.userId = accessToken.userId
        ContextUtil.userRole = accessToken.role
        ContextUtil.refreshTokenId = accessToken.refreshTokenId
    }

    private fun testUri(pattern: String, requestURI: String): Boolean {
        val patternRegex = pattern.replace("\\{[a-zA-Z]+}".toRegex(), "[^/?]+").toRegex()
        return patternRegex.matches(requestURI)
    }

    private fun getOperation(path: PathItem, method: String) = when(method) {
        "GET" -> path.get
        "PUT" -> path.put
        "POST" -> path.post
        "PATCH" -> path.patch
        "DELETE" -> path.delete
        else -> throw AuthenticationFailedException()
    }
}
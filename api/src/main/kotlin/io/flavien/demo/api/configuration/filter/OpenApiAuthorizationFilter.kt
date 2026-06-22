package io.flavien.demo.api.configuration.filter

import io.flavien.demo.api.session.util.ContextUtil
import io.flavien.demo.domain.permission.exception.BadPermissionException
import io.flavien.demo.domain.permission.model.PermissionEnum
import io.flavien.demo.domain.session.exception.AuthenticationFailedException
import io.flavien.demo.domain.session.service.AccessTokenService
import io.flavien.demo.domain.shared.util.SECURE_RANDOM
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.PathItem
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver
import java.io.IOException

@Component
class OpenApiAuthorizationFilter(
    openAPI: OpenAPI,
    private val accessTokenService: AccessTokenService,
    @Qualifier("handlerExceptionResolver")
    private val handlerExceptionResolver: HandlerExceptionResolver,
) : OncePerRequestFilter() {
    private val pathEntries: List<PathEntry> =
        openAPI.paths.map { (pattern, pathItem) ->
            PathEntry(
                regex = pattern.replace(OPEN_API_URL_VARIABLE_REGEX, "[^/?]+").toRegex(),
                pathItem = pathItem,
                variableCount = OPEN_API_URL_VARIABLE_REGEX.findAll(pattern).count(),
            )
        }

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
        } catch (exception: Exception) {
            log.warn("Authentication failed : ${exception.message}")

            // Waits a random amount of time to avoid time-based discovery attacks
            Thread.sleep(SECURE_RANDOM.nextLong(100))

            handlerExceptionResolver.resolveException(httpServletRequest, httpServletResponse, null, exception)
            return
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse)
    }

    private fun testApiSession(
        requestURI: String,
        method: String,
        httpServletRequest: HttpServletRequest,
    ) {
        val path =
            pathEntries
                .filter { it.regex.matches(requestURI) }
                .minByOrNull { it.variableCount }
                ?.pathItem ?: throw AuthenticationFailedException()

        val operation = getOperation(path, method) ?: throw AuthenticationFailedException()

        val securityRequirements =
            operation
                .security
                ?.flatMap { it.entries }

        if (securityRequirements.isNullOrEmpty()) {
            return
        }

        val authorizationHeader = httpServletRequest.getHeader("Authorization")
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw AuthenticationFailedException()
        }

        val bearer = authorizationHeader.removePrefix("Bearer ")
        val accessToken = accessTokenService.get(bearer)

        securityRequirements
            .filter { it.key == "bearer" }
            .flatMap { it.value }
            .map { scope -> toPermission(scope) }
            .forEach { permission ->
                if (permission !in accessToken.permissions) {
                    throw BadPermissionException(permission)
                }
            }

        ContextUtil.userId = accessToken.userId
        ContextUtil.refreshTokenId = accessToken.refreshTokenId
    }

    private fun toPermission(scope: String) =
        PermissionEnum.entries.firstOrNull { it.name == scope } ?: throw AuthenticationFailedException()

    private fun getOperation(
        path: PathItem,
        method: String,
    ) = when (method) {
        "GET" -> path.get
        "PUT" -> path.put
        "POST" -> path.post
        "PATCH" -> path.patch
        "DELETE" -> path.delete
        else -> null
    }

    private data class PathEntry(
        val regex: Regex,
        val pathItem: PathItem,
        val variableCount: Int,
    )

    companion object {
        private val log = LoggerFactory.getLogger(OpenApiAuthorizationFilter::class.java)
        private val OPEN_API_URL_VARIABLE_REGEX = "\\{[a-zA-Z]+}".toRegex()
    }
}

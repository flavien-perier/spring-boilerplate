package io.flavien.demo.api.configuration.filter

import io.flavien.demo.domain.tenant.TenantContext
import io.flavien.demo.domain.tenant.repository.TenantRegistry
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

@Component
class TenantResolutionFilter(
    private val tenantRegistry: TenantRegistry,
) : OncePerRequestFilter() {
    private val pathMatcher = AntPathMatcher()
    private val excludedPaths = listOf("/actuator/**")

    override fun shouldNotFilter(request: HttpServletRequest): Boolean = excludedPaths.any { pathMatcher.match(it, request.requestURI) }

    /**
     * The /error rendering opens a JPA EntityManager (open-in-view) which eagerly resolves
     * the current tenant, so the tenant context must also be set on ERROR dispatches.
     * Otherwise every error response is masked by a "Tenant not found: unset" failure.
     */
    override fun shouldNotFilterErrorDispatch(): Boolean = false

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val tenantId =
            request
                .getHeader("X-Tenant-Id")
                ?.takeIf { it.isNotBlank() }
                ?: DEFAULT_TENANT

        tenantRegistry.get(tenantId)

        TenantContext.set(tenantId)
        try {
            filterChain.doFilter(request, response)
        } finally {
            TenantContext.clear()
        }
    }

    companion object {
        const val DEFAULT_TENANT = "app"
    }
}

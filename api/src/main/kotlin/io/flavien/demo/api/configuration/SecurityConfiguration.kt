package io.flavien.demo.api.configuration

import io.flavien.demo.api.configuration.filter.CsrfCookieFilter
import io.flavien.demo.api.configuration.filter.OpenApiAuthorizationFilter
import io.flavien.demo.api.configuration.filter.TenantResolutionFilter
import jakarta.servlet.DispatcherType
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.csrf.CsrfFilter
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler

@EnableWebSecurity
@Configuration
class SecurityConfiguration(
    private val openApiAuthorizationFilter: OpenApiAuthorizationFilter,
    private val csrfCookieFilter: CsrfCookieFilter,
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .authorizeHttpRequests {
                it
                    .requestMatchers("/**")
                    .permitAll()
            }.cors { it.disable() }
            .csrf { csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                csrf.csrfTokenRequestHandler(CsrfTokenRequestAttributeHandler())
                csrf.requireCsrfProtectionMatcher(BearerTokenCsrfMatcher())
            }.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterAfter(csrfCookieFilter, CsrfFilter::class.java)
            .addFilterBefore(openApiAuthorizationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()

    @Bean
    fun csrfCookieFilterRegistration(): FilterRegistrationBean<CsrfCookieFilter> {
        val registration = FilterRegistrationBean(csrfCookieFilter)
        registration.isEnabled = false
        return registration
    }

    /**
     * The tenant filter must also cover ERROR dispatches: the /error rendering opens
     * a JPA EntityManager (open-in-view) which eagerly resolves the current tenant.
     * Without this registration, every error response would be masked by a
     * "Tenant not found: unset" failure.
     */
    @Bean
    fun tenantResolutionFilterRegistration(tenantResolutionFilter: TenantResolutionFilter): FilterRegistrationBean<TenantResolutionFilter> {
        val registration = FilterRegistrationBean(tenantResolutionFilter)
        registration.order = Ordered.HIGHEST_PRECEDENCE + 10
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR)
        return registration
    }
}

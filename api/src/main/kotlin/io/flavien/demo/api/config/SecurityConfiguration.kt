package io.flavien.demo.api.config

import io.flavien.demo.api.config.filter.CsrfCookieFilter
import io.flavien.demo.api.config.filter.OpenApiAuthorizationFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
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
                    .requestMatchers("/api/**")
                    .permitAll()
            }.cors { it.disable() }
            .csrf { csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                csrf.csrfTokenRequestHandler(CsrfTokenRequestAttributeHandler())
                csrf.requireCsrfProtectionMatcher(BearerTokenCsrfMatcher())
            }
            .httpBasic(Customizer.withDefaults())
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterAfter(csrfCookieFilter, CsrfFilter::class.java)
            .addFilterBefore(openApiAuthorizationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()

    @Bean
    fun csrfCookieFilterRegistration(): FilterRegistrationBean<CsrfCookieFilter> {
        val registration = FilterRegistrationBean(csrfCookieFilter)
        registration.isEnabled = false
        return registration
    }

    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager = authConfig.authenticationManager
}

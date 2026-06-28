package io.flavien.demo.api.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.resource.PathResourceResolver

/**
 * Serves the single-page application.
 *
 * Real static files (the Vite build output under `classpath:/static/`) are served as-is.
 * Any other non-API path falls back to `index.html` so the client-side router can take over.
 * This is what allows deep links such as `/admin/users/user@example.com` — whose last path
 * segment contains dots — to load instead of returning 404. Unmatched `/api` requests resolve
 * to no resource and therefore keep returning 404.
 */
@Configuration
class SpaResourceConfiguration : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // Do not add @Order: this configurer must run after Boot's default handler to win the "/**" mapping.
        registry
            .addResourceHandler("/**")
            .addResourceLocations(STATIC_LOCATION)
            .resourceChain(false)
            .addResolver(SpaResourceResolver())
    }

    private class SpaResourceResolver : PathResourceResolver() {
        override fun getResource(
            resourcePath: String,
            location: Resource,
        ): Resource? {
            if (resourcePath == "api" || resourcePath.startsWith("api/")) {
                return null
            }
            if (resourcePath.isBlank()) {
                return INDEX
            }

            val requested = location.createRelative(resourcePath)
            return if (requested.exists() && requested.isReadable && checkResource(requested, location)) {
                requested
            } else {
                INDEX
            }
        }
    }

    companion object {
        private const val STATIC_LOCATION = "classpath:/static/"
        private val INDEX = ClassPathResource("/static/index.html")
    }
}

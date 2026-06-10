package io.flavien.demo.api.configuration

import io.swagger.parser.OpenAPIParser
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints

@Configuration
@ImportRuntimeHints(ApiRuntimeHints::class)
class OpenAPIConfiguration {
    @Bean
    fun openAPI() =
        OpenAPIParser()
            .readLocation("openapi.json", null, null)
            .openAPI
}

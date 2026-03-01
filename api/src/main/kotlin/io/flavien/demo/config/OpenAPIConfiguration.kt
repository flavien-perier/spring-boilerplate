package io.flavien.demo.config

import io.swagger.parser.OpenAPIParser
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenAPIConfiguration {

    @Bean
    fun openAPI() =  OpenAPIParser()
        .readLocation("openapi.yaml", null, null)
        .openAPI
}
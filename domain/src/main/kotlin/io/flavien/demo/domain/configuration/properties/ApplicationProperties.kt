package io.flavien.demo.domain.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "flavien-io.application")
data class ApplicationProperties(
    val minPasswordLength: Int = 12,
)

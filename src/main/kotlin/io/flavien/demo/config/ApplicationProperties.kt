package io.flavien.demo.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "flavien-io.application")
data class ApplicationProperties(
    var minPasswordLength: Int = 0,
)
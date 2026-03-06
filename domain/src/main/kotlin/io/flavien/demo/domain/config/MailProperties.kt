package io.flavien.demo.domain.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "flavien-io.mail")
data class MailProperties(
    var accountCreator: String = "",
    var domainLinks: String = "",
)
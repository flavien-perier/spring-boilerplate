package io.flavien.demo.domain.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "flavien-io.tenants")
data class TenantProperties(
    val directory: String = "/etc/tenants",
)

package io.flavien.demo.domain.configuration

import io.flavien.demo.domain.configuration.properties.ApplicationProperties
import io.flavien.demo.domain.configuration.properties.TenantProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ApplicationProperties::class, TenantProperties::class)
class PropertiesConfiguration

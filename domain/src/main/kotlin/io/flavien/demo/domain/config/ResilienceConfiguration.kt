package io.flavien.demo.domain.config

import org.springframework.context.annotation.Configuration
import org.springframework.retry.annotation.EnableRetry

@Configuration
@EnableRetry
class ResilienceConfiguration

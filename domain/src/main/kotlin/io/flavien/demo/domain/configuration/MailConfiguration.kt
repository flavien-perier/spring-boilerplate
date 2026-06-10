package io.flavien.demo.domain.configuration

import io.flavien.demo.domain.tenant.configuration.TenantJavaMailSender
import io.flavien.demo.domain.tenant.repository.TenantRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.mail.javamail.JavaMailSender

@Configuration
class MailConfiguration {
    @Bean
    @Primary
    fun javaMailSender(registry: TenantRegistry): JavaMailSender = TenantJavaMailSender(registry)
}

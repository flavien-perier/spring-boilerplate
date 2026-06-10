package io.flavien.demo.domain.shared.service

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class MailService(
    private val emailSender: JavaMailSender,
) {
    @Retry(name = "mail")
    @CircuitBreaker(name = "mail")
    fun send(message: SimpleMailMessage) = emailSender.send(message)
}

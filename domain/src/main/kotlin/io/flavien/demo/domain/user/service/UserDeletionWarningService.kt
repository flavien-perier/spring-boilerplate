package io.flavien.demo.domain.user.service

import io.flavien.demo.domain.config.MailProperties
import io.flavien.demo.domain.user.entity.User
import io.flavien.demo.domain.user.repository.UserRepository
import io.github.resilience4j.retry.annotation.Retry
import org.slf4j.LoggerFactory
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.time.OffsetDateTime

@Service
class UserDeletionWarningService(
    private val userRepository: UserRepository,
    private val templateEngine: TemplateEngine,
    private val emailSender: JavaMailSender,
    private val mailProperties: MailProperties,
) {
    @Retry(name = "mailSend")
    fun sendWarningEmail(user: User) {
        val context = Context()
        context.setVariable("domainLinks", mailProperties.domainLinks)
        context.setVariable("email", user.email)

        val message = SimpleMailMessage()
        message.from = mailProperties.accountCreator
        message.setTo(user.email)
        message.subject = "Account deletion warning"
        message.text = templateEngine.process("user-deletion-warning", context)
        emailSender.send(message)

        user.deletionWarningSentAt = OffsetDateTime.now()
        userRepository.save(user)
        logger.info("Sent deletion warning email to ${user.email}")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UserDeletionWarningService::class.java)
    }
}

package io.flavien.demo.domain.user.service

import io.flavien.demo.domain.config.MailProperties
import io.flavien.demo.domain.user.entity.ForgotPassword
import io.flavien.demo.domain.user.entity.User
import io.flavien.demo.domain.user.exception.ChangePasswordFailedException
import io.flavien.demo.domain.user.repository.ForgotPasswordRepository
import io.flavien.demo.utils.RandomUtil
import org.slf4j.LoggerFactory
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import kotlin.jvm.optionals.getOrNull

@Service
class ForgotPasswordService(
    private val forgotPasswordRepository: ForgotPasswordRepository,
    private val templateEngine: TemplateEngine,
    private val emailSender: JavaMailSender,
    private val mailProperties: MailProperties,
) {
    @Retryable(include = [org.springframework.mail.MailException::class], maxAttempts = 3, backoff = Backoff(delay = 500))
    fun sendForgotPasswordToken(user: User) {
        var forgotPasswordToken = RandomUtil.randomString(64)
        while (forgotPasswordRepository.existsById(forgotPasswordToken)) {
            forgotPasswordToken = RandomUtil.randomString(64)
        }

        val forgotPassword = ForgotPassword(forgotPasswordToken, user.id!!)
        forgotPasswordRepository.save(forgotPassword)

        val context = Context()
        context.setVariable("domainLinks", mailProperties.domainLinks)
        context.setVariable("email", user.email)
        context.setVariable("forgotPasswordToken", forgotPasswordToken)

        val message = SimpleMailMessage()
        message.from = mailProperties.accountCreator
        message.setTo(user.email)
        message.subject = "Change password"
        message.text = templateEngine.process("forgot-password", context)
        emailSender.send(message)
        logger.info("Sent forgot password email to ${user.email}")
    }

    @Retryable(include = [org.springframework.mail.MailException::class], maxAttempts = 3, backoff = Backoff(delay = 500))
    fun validate(token: String): ForgotPassword {
        val forgotPassword = forgotPasswordRepository.findById(token).getOrNull() ?: throw ChangePasswordFailedException()

        forgotPasswordRepository.delete(forgotPassword)

        return forgotPassword
    }

    @Retryable(include = [org.springframework.mail.MailException::class], maxAttempts = 3, backoff = Backoff(delay = 500))
    fun deleteByUserId(userId: Long) {
        forgotPasswordRepository.deleteByUserId(userId)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ForgotPasswordService::class.java)
    }
}

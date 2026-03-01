package io.flavien.demo.user.service

import io.flavien.demo.config.MailProperties
import io.flavien.demo.core.util.RandomUtil
import io.flavien.demo.user.entity.ForgotPassword
import io.flavien.demo.user.entity.User
import io.flavien.demo.user.exception.ChangePasswordFailedException
import io.flavien.demo.user.repository.ForgotPasswordRepository
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
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
    }

    fun validate(token: String): ForgotPassword {
        val forgotPassword = forgotPasswordRepository.findById(token).getOrNull() ?: throw ChangePasswordFailedException()

        forgotPasswordRepository.delete(forgotPassword)

        return forgotPassword
    }


    fun deleteByUserId(userId: Long) {
        forgotPasswordRepository.deleteByUserId(userId)
    }
}
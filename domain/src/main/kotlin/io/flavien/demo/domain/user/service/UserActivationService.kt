package io.flavien.demo.domain.user.service

import io.flavien.demo.domain.config.MailProperties
import io.flavien.demo.domain.user.entity.User
import io.flavien.demo.domain.user.entity.UserActivation
import io.flavien.demo.domain.user.exception.ActivationFailedException
import io.flavien.demo.domain.user.repository.UserActivationRepository
import io.flavien.demo.utils.RandomUtil
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import kotlin.jvm.optionals.getOrNull


@Service
class UserActivationService(
    private val userActivationRepository: UserActivationRepository,
    private val templateEngine: TemplateEngine,
    private val emailSender: JavaMailSender,
    private val mailProperties: MailProperties,
) {

    fun sendActivationToken(user: User) {
        var activationToken = RandomUtil.randomString(64)
        while (userActivationRepository.existsById(activationToken)) {
            activationToken = RandomUtil.randomString(64)
        }

        val userActivation = UserActivation(activationToken, user.id!!)
        userActivationRepository.save(userActivation)

        val context = Context()
        context.setVariable("domainLinks", mailProperties.domainLinks)
        context.setVariable("activationToken", activationToken)

        val message = SimpleMailMessage()
        message.from = mailProperties.accountCreator
        message.setTo(user.email)
        message.subject = "Activation code"
        message.text = templateEngine.process("user-activation", context)
        emailSender.send(message)
    }

    fun validate(token: String): UserActivation {
        val userActivation = userActivationRepository.findById(token).getOrNull() ?: throw ActivationFailedException()

        userActivationRepository.delete(userActivation)

        return userActivation
    }

    fun deleteByUserId(userId: Long) {
        userActivationRepository.deleteByUserId(userId)
    }
}
package io.flavien.demo.domain.user.service

import io.flavien.demo.domain.shared.service.MailService
import io.flavien.demo.domain.shared.util.SECURE_RANDOM
import io.flavien.demo.domain.tenant.TenantContext
import io.flavien.demo.domain.tenant.repository.TenantRegistry
import io.flavien.demo.domain.user.entity.User
import io.flavien.demo.domain.user.entity.UserActivation
import io.flavien.demo.domain.user.exception.ActivationFailedException
import io.flavien.demo.domain.user.repository.UserActivationRepository
import io.flavien.demo.library.common.RandomUtil
import org.slf4j.LoggerFactory
import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import kotlin.jvm.optionals.getOrNull

@Service
class UserActivationService(
    private val userActivationRepository: UserActivationRepository,
    private val templateEngine: TemplateEngine,
    private val mailService: MailService,
    private val registry: TenantRegistry,
) {
    fun sendActivationToken(user: User) {
        val activationToken = RandomUtil.randomString(64, SECURE_RANDOM)

        val userActivation = UserActivation(activationToken, user.id!!)
        userActivationRepository.save(userActivation)

        val smtp = registry.get(TenantContext.require()).smtp

        val context = Context()
        context.setVariable("domainLinks", smtp.domainLinks)
        context.setVariable("activationToken", activationToken)

        val message = SimpleMailMessage()
        message.from = smtp.accountCreator
        message.setTo(user.email)
        message.subject = "Activation code"
        message.text = templateEngine.process("user-activation", context)
        mailService.send(message)
        log.info("Sent activation email to ${user.email}")
    }

    fun validate(token: String): UserActivation {
        val userActivation = userActivationRepository.findById(token).getOrNull() ?: throw ActivationFailedException()

        userActivationRepository.delete(userActivation)

        return userActivation
    }

    fun deleteByUserId(userId: Long) {
        userActivationRepository.deleteByUserId(userId)
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserActivationService::class.java)
    }
}

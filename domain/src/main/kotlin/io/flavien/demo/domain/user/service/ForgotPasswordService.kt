package io.flavien.demo.domain.user.service

import io.flavien.demo.domain.shared.service.MailService
import io.flavien.demo.domain.shared.util.SECURE_RANDOM
import io.flavien.demo.domain.tenant.TenantContext
import io.flavien.demo.domain.tenant.repository.TenantRegistry
import io.flavien.demo.domain.user.entity.ForgotPassword
import io.flavien.demo.domain.user.entity.User
import io.flavien.demo.domain.user.exception.ChangePasswordFailedException
import io.flavien.demo.domain.user.repository.ForgotPasswordRepository
import io.flavien.demo.library.common.RandomUtil
import org.slf4j.LoggerFactory
import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Service
class ForgotPasswordService(
    private val forgotPasswordRepository: ForgotPasswordRepository,
    private val templateEngine: TemplateEngine,
    private val mailService: MailService,
    private val registry: TenantRegistry,
) {
    fun sendForgotPasswordToken(user: User) {
        val forgotPasswordToken = RandomUtil.randomString(64, SECURE_RANDOM)

        val forgotPassword = ForgotPassword(forgotPasswordToken, user.id!!.toString())
        forgotPasswordRepository.save(forgotPassword)

        val smtp = registry.get(TenantContext.require()).smtp

        val context = Context()
        context.setVariable("domainLinks", smtp.domainLinks)
        context.setVariable("email", user.email)
        context.setVariable("forgotPasswordToken", forgotPasswordToken)

        val message = SimpleMailMessage()
        message.from = smtp.accountCreator
        message.setTo(user.email)
        message.subject = "Change password"
        message.text = templateEngine.process("forgot-password", context)
        mailService.send(message)
        log.info("Sent forgot password email to ${user.email}")
    }

    fun validate(token: String): ForgotPassword {
        val forgotPassword = forgotPasswordRepository.findById(token).getOrNull() ?: throw ChangePasswordFailedException()

        forgotPasswordRepository.delete(forgotPassword)

        return forgotPassword
    }

    fun deleteByUserId(userId: UUID) {
        forgotPasswordRepository.deleteByUserId(userId.toString())
    }

    companion object {
        private val log = LoggerFactory.getLogger(ForgotPasswordService::class.java)
    }
}

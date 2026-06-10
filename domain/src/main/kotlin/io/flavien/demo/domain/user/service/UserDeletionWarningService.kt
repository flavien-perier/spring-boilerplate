package io.flavien.demo.domain.user.service

import io.flavien.demo.domain.shared.service.MailService
import io.flavien.demo.domain.tenant.TenantContext
import io.flavien.demo.domain.tenant.repository.TenantRegistry
import io.flavien.demo.domain.user.entity.User
import io.flavien.demo.domain.user.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.time.OffsetDateTime

@Service
class UserDeletionWarningService(
    private val userRepository: UserRepository,
    private val templateEngine: TemplateEngine,
    private val mailService: MailService,
    private val registry: TenantRegistry,
) {
    fun sendWarningEmail(user: User) {
        val smtp = registry.get(TenantContext.require()).smtp

        val context = Context()
        context.setVariable("domainLinks", smtp.domainLinks)
        context.setVariable("email", user.email)

        val message = SimpleMailMessage()
        message.from = smtp.accountCreator
        message.setTo(user.email)
        message.subject = "Account deletion warning"
        message.text = templateEngine.process("user-deletion-warning", context)
        mailService.send(message)

        user.deletionWarningSentAt = OffsetDateTime.now()
        userRepository.save(user)
        log.info("Sent deletion warning email to ${user.email}")
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserDeletionWarningService::class.java)
    }
}

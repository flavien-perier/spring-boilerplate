package io.flavien.demo.domain.tenant.configuration

import io.flavien.demo.domain.tenant.TenantContext
import io.flavien.demo.domain.tenant.repository.TenantRegistry
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.MailException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.concurrent.ConcurrentHashMap

class TenantJavaMailSender(
    private val registry: TenantRegistry,
) : JavaMailSender {
    private val senders = ConcurrentHashMap<String, JavaMailSenderImpl>()

    internal fun getOrCreate(tenantId: String): JavaMailSenderImpl =
        senders.computeIfAbsent(tenantId) { id ->
            val smtp = registry.get(id).smtp
            JavaMailSenderImpl().apply {
                host = smtp.host
                port = smtp.port
                username = smtp.username
                password = smtp.password
                javaMailProperties["mail.smtp.auth"] = smtp.auth.toString()
                javaMailProperties["mail.smtp.starttls.enable"] = smtp.starttls.toString()
            }
        }

    private fun resolvedSender(): JavaMailSenderImpl = getOrCreate(TenantContext.require())

    override fun createMimeMessage(): MimeMessage = resolvedSender().createMimeMessage()

    override fun createMimeMessage(contentStream: java.io.InputStream): MimeMessage = resolvedSender().createMimeMessage(contentStream)

    @Throws(MailException::class)
    override fun send(mimeMessage: MimeMessage) = resolvedSender().send(mimeMessage)

    @Throws(MailException::class)
    override fun send(vararg mimeMessages: MimeMessage) = resolvedSender().send(*mimeMessages)

    @Throws(MailException::class)
    override fun send(mimeMessagePreparator: org.springframework.mail.javamail.MimeMessagePreparator) =
        resolvedSender().send(mimeMessagePreparator)

    @Throws(MailException::class)
    override fun send(vararg mimeMessagePreparators: org.springframework.mail.javamail.MimeMessagePreparator) =
        resolvedSender().send(*mimeMessagePreparators)

    @Throws(MailException::class)
    override fun send(simpleMessage: SimpleMailMessage) = resolvedSender().send(simpleMessage)

    @Throws(MailException::class)
    override fun send(vararg simpleMessages: SimpleMailMessage) = resolvedSender().send(*simpleMessages)
}

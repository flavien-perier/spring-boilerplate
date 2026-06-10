package io.flavien.demo.domain.user.service

import io.flavien.demo.domain.shared.service.MailService
import io.flavien.demo.domain.tenant.TenantContext
import io.flavien.demo.domain.tenant.model.DbDefinition
import io.flavien.demo.domain.tenant.model.RedisDefinition
import io.flavien.demo.domain.tenant.model.SmtpDefinition
import io.flavien.demo.domain.tenant.model.TenantDefinition
import io.flavien.demo.domain.tenant.repository.TenantRegistry
import io.flavien.demo.domain.user.UserTestFactory
import io.flavien.demo.domain.user.entity.ForgotPassword
import io.flavien.demo.domain.user.exception.ChangePasswordFailedException
import io.flavien.demo.domain.user.repository.ForgotPasswordRepository
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mail.SimpleMailMessage
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.Optional

class ForgotPasswordServiceTest {
    private val forgotPasswordRepository = mockk<ForgotPasswordRepository>(relaxed = true)
    private val templateEngine = mockk<TemplateEngine>()
    private val mailService = mockk<MailService>()
    private val registry = mockk<TenantRegistry>()

    private val forgotPasswordService =
        ForgotPasswordService(forgotPasswordRepository, templateEngine, mailService, registry)

    private val testTenant =
        TenantDefinition(
            tenantId = "test",
            db = DbDefinition(jdbcUrl = "jdbc:postgresql://localhost/test", username = "user", password = "pass", schema = "test"),
            redis = RedisDefinition(host = "localhost"),
            smtp = SmtpDefinition(host = "localhost", accountCreator = "no-reply@flavien.io", domainLinks = "https://flavien.io"),
        )

    @BeforeEach
    fun setUp() {
        TenantContext.set("test")
    }

    @AfterEach
    fun tearDown() {
        TenantContext.clear()
    }

    @Test
    fun `Should send forgot-password email and persist token`() {
        // Given
        val user = UserTestFactory.initUser(email = "user@flavien.io", id = 42L)
        every { registry.get("test") } returns testTenant
        every { templateEngine.process(eq("forgot-password"), any<Context>()) } returns "<html>Reset body</html>"
        val forgotPasswordSlot = slot<ForgotPassword>()
        every { forgotPasswordRepository.save(capture(forgotPasswordSlot)) } answers { forgotPasswordSlot.captured }
        val messageSlot = slot<SimpleMailMessage>()
        justRun { mailService.send(capture(messageSlot)) }

        // When
        forgotPasswordService.sendForgotPasswordToken(user)

        // Then
        assertThat(forgotPasswordSlot.captured.userId).isEqualTo(42L)
        assertThat(forgotPasswordSlot.captured.id).isNotBlank()

        assertThat(messageSlot.captured.to).containsExactly("user@flavien.io")
        assertThat(messageSlot.captured.from).isEqualTo("no-reply@flavien.io")
        assertThat(messageSlot.captured.subject).isEqualTo("Change password")
        verify(exactly = 1) { mailService.send(any()) }
    }

    @Test
    fun `Should validate and return forgot-password token`() {
        // Given
        val forgotPassword = UserTestFactory.initForgotPassword()
        every { forgotPasswordRepository.findById(forgotPassword.id) } returns Optional.of(forgotPassword)

        // When
        val result = forgotPasswordService.validate(forgotPassword.id)

        // Then
        assertThat(result).isEqualTo(forgotPassword)
        verify(exactly = 1) { forgotPasswordRepository.findById(forgotPassword.id) }
        verify(exactly = 1) { forgotPasswordRepository.delete(forgotPassword) }
    }

    @Test
    fun `Should fail to validate an unknown forgot-password token`() {
        // Given
        val unknownToken = "non-existent-token"
        every { forgotPasswordRepository.findById(unknownToken) } returns Optional.empty()

        // When / Then
        assertThatThrownBy { forgotPasswordService.validate(unknownToken) }
            .isInstanceOf(ChangePasswordFailedException::class.java)

        verify(exactly = 1) { forgotPasswordRepository.findById(unknownToken) }
        verify(exactly = 0) { forgotPasswordRepository.delete(any<ForgotPassword>()) }
    }

    @Test
    fun `Should delete forgot-password tokens by user id`() {
        // Given
        val userId = 7L

        // When
        forgotPasswordService.deleteByUserId(userId)

        // Then
        verify(exactly = 1) { forgotPasswordRepository.deleteByUserId(userId) }
    }
}

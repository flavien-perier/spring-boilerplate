package io.flavien.demo.domain.user.service

import io.flavien.demo.domain.shared.service.MailService
import io.flavien.demo.domain.tenant.TenantContext
import io.flavien.demo.domain.tenant.model.DbDefinition
import io.flavien.demo.domain.tenant.model.RedisDefinition
import io.flavien.demo.domain.tenant.model.SmtpDefinition
import io.flavien.demo.domain.tenant.model.TenantDefinition
import io.flavien.demo.domain.tenant.repository.TenantRegistry
import io.flavien.demo.domain.user.UserTestFactory
import io.flavien.demo.domain.user.entity.User
import io.flavien.demo.domain.user.repository.UserRepository
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mail.SimpleMailMessage
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

class UserDeletionWarningServiceTest {
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val templateEngine = mockk<TemplateEngine>()
    private val mailService = mockk<MailService>()
    private val registry = mockk<TenantRegistry>()

    private val userDeletionWarningService =
        UserDeletionWarningService(userRepository, templateEngine, mailService, registry)

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
    fun `Should send warning email to inactive user and update deletionWarningSentAt`() {
        // Given
        val user = UserTestFactory.initUser(email = "inactive@flavien.io")
        every { registry.get("test") } returns testTenant
        every { templateEngine.process(eq("user-deletion-warning"), any<Context>()) } returns "<html>Warning body</html>"
        val messageSlot = slot<SimpleMailMessage>()
        justRun { mailService.send(capture(messageSlot)) }
        val userSlot = slot<User>()
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }

        // When
        userDeletionWarningService.sendWarningEmail(user)

        // Then
        assertThat(messageSlot.captured.to).containsExactly("inactive@flavien.io")
        assertThat(messageSlot.captured.from).isEqualTo("no-reply@flavien.io")

        assertThat(userSlot.captured.deletionWarningSentAt).isNotNull()
        verify(exactly = 1) { mailService.send(any()) }
    }
}

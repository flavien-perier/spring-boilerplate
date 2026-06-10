package io.flavien.demo.domain.user.service

import io.flavien.demo.domain.shared.service.MailService
import io.flavien.demo.domain.tenant.TenantContext
import io.flavien.demo.domain.tenant.model.DbDefinition
import io.flavien.demo.domain.tenant.model.RedisDefinition
import io.flavien.demo.domain.tenant.model.SmtpDefinition
import io.flavien.demo.domain.tenant.model.TenantDefinition
import io.flavien.demo.domain.tenant.repository.TenantRegistry
import io.flavien.demo.domain.user.UserTestFactory
import io.flavien.demo.domain.user.entity.UserActivation
import io.flavien.demo.domain.user.exception.ActivationFailedException
import io.flavien.demo.domain.user.repository.UserActivationRepository
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

class UserActivationServiceTest {
    private val userActivationRepository = mockk<UserActivationRepository>(relaxed = true)
    private val templateEngine = mockk<TemplateEngine>()
    private val mailService = mockk<MailService>()
    private val registry = mockk<TenantRegistry>()

    private val userActivationService =
        UserActivationService(userActivationRepository, templateEngine, mailService, registry)

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
    fun `Should send activation email and persist activation token`() {
        // Given
        val user = UserTestFactory.initUser(email = "new@flavien.io", id = 10L)
        every { registry.get("test") } returns testTenant
        every { templateEngine.process(eq("user-activation"), any<Context>()) } returns "<html>Activation body</html>"
        val activationSlot = slot<UserActivation>()
        every { userActivationRepository.save(capture(activationSlot)) } answers { activationSlot.captured }
        val messageSlot = slot<SimpleMailMessage>()
        justRun { mailService.send(capture(messageSlot)) }

        // When
        userActivationService.sendActivationToken(user)

        // Then
        assertThat(activationSlot.captured.userId).isEqualTo(10L)
        assertThat(activationSlot.captured.id).isNotBlank()

        assertThat(messageSlot.captured.to).containsExactly("new@flavien.io")
        assertThat(messageSlot.captured.from).isEqualTo("no-reply@flavien.io")
        assertThat(messageSlot.captured.subject).isEqualTo("Activation code")
        verify(exactly = 1) { mailService.send(any()) }
    }

    @Test
    fun `Should validate and return activation token`() {
        // Given
        val userActivation = UserTestFactory.initUserActivation()
        every { userActivationRepository.findById(userActivation.id) } returns Optional.of(userActivation)

        // When
        val result = userActivationService.validate(userActivation.id)

        // Then
        assertThat(result).isEqualTo(userActivation)
        verify(exactly = 1) { userActivationRepository.findById(userActivation.id) }
        verify(exactly = 1) { userActivationRepository.delete(userActivation) }
    }

    @Test
    fun `Should fail to validate an unknown activation token`() {
        // Given
        val unknownToken = "non-existent-token"
        every { userActivationRepository.findById(unknownToken) } returns Optional.empty()

        // When / Then
        assertThatThrownBy { userActivationService.validate(unknownToken) }
            .isInstanceOf(ActivationFailedException::class.java)

        verify(exactly = 1) { userActivationRepository.findById(unknownToken) }
        verify(exactly = 0) { userActivationRepository.delete(any<UserActivation>()) }
    }

    @Test
    fun `Should delete activation tokens by user id`() {
        // Given
        val userId = 10L

        // When
        userActivationService.deleteByUserId(userId)

        // Then
        verify(exactly = 1) { userActivationRepository.deleteByUserId(userId) }
    }
}

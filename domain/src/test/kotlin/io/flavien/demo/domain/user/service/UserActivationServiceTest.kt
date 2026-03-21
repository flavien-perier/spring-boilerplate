package io.flavien.demo.domain.user.service

import io.flavien.demo.domain.config.MailProperties
import io.flavien.demo.domain.user.UserTestFactory
import io.flavien.demo.domain.user.entity.UserActivation
import io.flavien.demo.domain.user.exception.ActivationFailedException
import io.flavien.demo.domain.user.repository.UserActivationRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.atLeast
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.Optional

// NOTE – The @Retry(name = "mailSend") annotation on sendActivationToken works via Spring AOP
// proxies and has NO effect in this plain Mockito unit test where the service is instantiated
// directly. Retry behaviour is validated by E2E / integration tests that boot a full Spring context
// with Resilience4j configured. The tests below verify the nominal path and business logic only.

@ExtendWith(MockitoExtension::class)
class UserActivationServiceTest {
    @InjectMocks
    lateinit var userActivationService: UserActivationService

    @Mock
    lateinit var userActivationRepository: UserActivationRepository

    @Mock
    lateinit var templateEngine: TemplateEngine

    @Mock
    lateinit var emailSender: JavaMailSender

    @Mock
    lateinit var mailProperties: MailProperties

    @Test
    fun `Should send activation email and persist activation token`() {
        // Given
        val user = UserTestFactory.initUser(email = "new@flavien.io", id = 10L)
        `when`(userActivationRepository.existsById(anyString())).thenReturn(false)
        `when`(mailProperties.accountCreator).thenReturn("no-reply@flavien.io")
        `when`(mailProperties.domainLinks).thenReturn("https://flavien.io")
        `when`(templateEngine.process(eq("user-activation"), any(Context::class.java)))
            .thenReturn("<html>Activation body</html>")

        // When
        userActivationService.sendActivationToken(user)

        // Then
        val activationCaptor = ArgumentCaptor.forClass(UserActivation::class.java)
        verify(userActivationRepository).save(activationCaptor.capture())
        assertThat(activationCaptor.value.userId).isEqualTo(10L)
        assertThat(activationCaptor.value.id).isNotBlank()

        val messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage::class.java)
        verify(emailSender).send(messageCaptor.capture())
        assertThat(messageCaptor.value.to).containsExactly("new@flavien.io")
        assertThat(messageCaptor.value.from).isEqualTo("no-reply@flavien.io")
        assertThat(messageCaptor.value.subject).isEqualTo("Activation code")
    }

    @Test
    fun `Should retry token generation when generated activation token already exists`() {
        // Given – first generated token collides, second one is free
        val user = UserTestFactory.initUser(email = "new@flavien.io", id = 10L)
        `when`(userActivationRepository.existsById(anyString())).thenReturn(true, false)
        `when`(mailProperties.accountCreator).thenReturn("no-reply@flavien.io")
        `when`(mailProperties.domainLinks).thenReturn("https://flavien.io")
        `when`(templateEngine.process(eq("user-activation"), any(Context::class.java)))
            .thenReturn("<html>Activation body</html>")

        // When
        userActivationService.sendActivationToken(user)

        // Then – existsById was called at least twice (collision + free slot)
        verify(userActivationRepository, atLeast(2)).existsById(anyString())
        verify(userActivationRepository).save(any(UserActivation::class.java))
    }

    @Test
    fun `Should validate and return activation token`() {
        // Given
        val userActivation = UserTestFactory.initUserActivation()
        `when`(userActivationRepository.findById(userActivation.id))
            .thenReturn(Optional.of(userActivation))

        // When
        val result = userActivationService.validate(userActivation.id)

        // Then
        assertThat(result).isEqualTo(userActivation)
        verify(userActivationRepository).findById(userActivation.id)
        verify(userActivationRepository).delete(userActivation)
    }

    @Test
    fun `Should fail to validate an unknown activation token`() {
        // Given
        val unknownToken = "non-existent-token"
        `when`(userActivationRepository.findById(unknownToken)).thenReturn(Optional.empty())

        // When / Then
        assertThrows(ActivationFailedException::class.java) {
            userActivationService.validate(unknownToken)
        }

        verify(userActivationRepository).findById(unknownToken)
        verify(userActivationRepository, never()).delete(any(UserActivation::class.java))
    }

    @Test
    fun `Should delete activation tokens by user id`() {
        // Given
        val userId = 10L

        // When
        userActivationService.deleteByUserId(userId)

        // Then
        verify(userActivationRepository).deleteByUserId(userId)
    }
}

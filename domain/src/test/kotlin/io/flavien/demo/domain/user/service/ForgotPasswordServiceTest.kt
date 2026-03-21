package io.flavien.demo.domain.user.service

import io.flavien.demo.domain.config.MailProperties
import io.flavien.demo.domain.user.UserTestFactory
import io.flavien.demo.domain.user.entity.ForgotPassword
import io.flavien.demo.domain.user.exception.ChangePasswordFailedException
import io.flavien.demo.domain.user.repository.ForgotPasswordRepository
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
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.Optional

// NOTE – The @Retry(name = "mailSend") annotation on sendForgotPasswordToken works via Spring AOP
// proxies and has NO effect in this plain Mockito unit test where the service is instantiated
// directly. Retry behaviour is validated by E2E / integration tests that boot a full Spring context
// with Resilience4j configured. The tests below verify the nominal path and business logic only.

@ExtendWith(MockitoExtension::class)
class ForgotPasswordServiceTest {
    @InjectMocks
    lateinit var forgotPasswordService: ForgotPasswordService

    @Mock
    lateinit var forgotPasswordRepository: ForgotPasswordRepository

    @Mock
    lateinit var templateEngine: TemplateEngine

    @Mock
    lateinit var emailSender: JavaMailSender

    @Mock
    lateinit var mailProperties: MailProperties

    @Test
    fun `Should send forgot-password email and persist token`() {
        // Given
        val user = UserTestFactory.initUser(email = "user@flavien.io", id = 42L)
        `when`(forgotPasswordRepository.existsById(anyString())).thenReturn(false)
        `when`(mailProperties.accountCreator).thenReturn("no-reply@flavien.io")
        `when`(mailProperties.domainLinks).thenReturn("https://flavien.io")
        `when`(templateEngine.process(eq("forgot-password"), any(Context::class.java)))
            .thenReturn("<html>Reset body</html>")

        // When
        forgotPasswordService.sendForgotPasswordToken(user)

        // Then
        val forgotPasswordCaptor = ArgumentCaptor.forClass(ForgotPassword::class.java)
        verify(forgotPasswordRepository).save(forgotPasswordCaptor.capture())
        assertThat(forgotPasswordCaptor.value.userId).isEqualTo(42L)
        assertThat(forgotPasswordCaptor.value.id).isNotBlank()

        val messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage::class.java)
        verify(emailSender).send(messageCaptor.capture())
        assertThat(messageCaptor.value.to).containsExactly("user@flavien.io")
        assertThat(messageCaptor.value.from).isEqualTo("no-reply@flavien.io")
        assertThat(messageCaptor.value.subject).isEqualTo("Change password")
    }

    @Test
    fun `Should retry token generation when generated token already exists`() {
        // Given – first generated token already exists, second one is free
        val user = UserTestFactory.initUser(email = "user@flavien.io", id = 42L)
        `when`(forgotPasswordRepository.existsById(anyString())).thenReturn(true, false)
        `when`(mailProperties.accountCreator).thenReturn("no-reply@flavien.io")
        `when`(mailProperties.domainLinks).thenReturn("https://flavien.io")
        `when`(templateEngine.process(eq("forgot-password"), any(Context::class.java)))
            .thenReturn("<html>Reset body</html>")

        // When
        forgotPasswordService.sendForgotPasswordToken(user)

        // Then – existsById was called at least twice (collision + free slot)
        verify(forgotPasswordRepository, org.mockito.Mockito.atLeast(2)).existsById(anyString())
        verify(forgotPasswordRepository).save(any(ForgotPassword::class.java))
    }

    @Test
    fun `Should validate and return forgot-password token`() {
        // Given
        val forgotPassword = UserTestFactory.initForgotPassword()
        `when`(forgotPasswordRepository.findById(forgotPassword.id))
            .thenReturn(Optional.of(forgotPassword))

        // When
        val result = forgotPasswordService.validate(forgotPassword.id)

        // Then
        assertThat(result).isEqualTo(forgotPassword)
        verify(forgotPasswordRepository).findById(forgotPassword.id)
        verify(forgotPasswordRepository).delete(forgotPassword)
    }

    @Test
    fun `Should fail to validate an unknown forgot-password token`() {
        // Given
        val unknownToken = "non-existent-token"
        `when`(forgotPasswordRepository.findById(unknownToken)).thenReturn(Optional.empty())

        // When / Then
        assertThrows(ChangePasswordFailedException::class.java) {
            forgotPasswordService.validate(unknownToken)
        }

        verify(forgotPasswordRepository).findById(unknownToken)
        verify(forgotPasswordRepository, never()).delete(any(ForgotPassword::class.java))
    }

    @Test
    fun `Should delete forgot-password tokens by user id`() {
        // Given
        val userId = 7L

        // When
        forgotPasswordService.deleteByUserId(userId)

        // Then
        verify(forgotPasswordRepository).deleteByUserId(userId)
    }
}

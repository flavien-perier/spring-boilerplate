package io.flavien.demo.domain.user.service

import io.flavien.demo.domain.config.MailProperties
import io.flavien.demo.domain.user.UserTestFactory
import io.flavien.demo.domain.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

// NOTE – The @Retry(name = "mailSend") annotation on sendWarningEmail works via Spring AOP proxies
// and has NO effect in this plain Mockito unit test where the service is instantiated directly.
// Retry behaviour (i.e. that emailSender.send() is called up to N times on MailException) is
// validated by E2E / integration tests that boot a full Spring context with Resilience4j configured.
// The tests below verify the nominal (success) path and that the correct mail content is produced.

@ExtendWith(MockitoExtension::class)
class UserDeletionWarningServiceTest {
    @InjectMocks
    lateinit var userDeletionWarningService: UserDeletionWarningService

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var templateEngine: TemplateEngine

    @Mock
    lateinit var emailSender: JavaMailSender

    @Mock
    lateinit var mailProperties: MailProperties

    @Test
    fun `Should send warning email to inactive user and update deletionWarningSentAt`() {
        // Given
        val user = UserTestFactory.initUser(email = "inactive@flavien.io")
        `when`(mailProperties.accountCreator).thenReturn("no-reply@flavien.io")
        `when`(mailProperties.domainLinks).thenReturn("https://flavien.io")
        `when`(templateEngine.process(eq("user-deletion-warning"), any(Context::class.java)))
            .thenReturn("<html>Warning body</html>")

        // When
        userDeletionWarningService.sendWarningEmail(user)

        // Then
        val messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage::class.java)
        verify(emailSender).send(messageCaptor.capture())
        assertThat(messageCaptor.value.to).containsExactly("inactive@flavien.io")
        assertThat(messageCaptor.value.from).isEqualTo("no-reply@flavien.io")

        val userCaptor = ArgumentCaptor.forClass(io.flavien.demo.domain.user.entity.User::class.java)
        verify(userRepository).save(userCaptor.capture())
        assertThat(userCaptor.value.deletionWarningSentAt).isNotNull()
    }
}

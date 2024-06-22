package io.flavien.demo.user.service

import io.flavien.demo.config.MailProperties
import io.flavien.demo.user.repository.UserActivationRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mail.javamail.JavaMailSender
import org.thymeleaf.TemplateEngine

@ExtendWith(MockitoExtension::class)
class UserActivationServiceTest {

    @InjectMocks
    var userActivationService: UserActivationService? = null

    @Mock
    var userActivationRepository: UserActivationRepository? = null

    @Mock
    var templateEngine: TemplateEngine? = null

    @Mock
    var emailSender: JavaMailSender? = null

    @Mock
    var mailProperties: MailProperties? = null

    // TODO: Implement tests
    @Test
    fun `Should send an activation token by email`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should validate an activation token`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should delete activation token by userId`() {
        // Given

        // When

        // Then
    }
}
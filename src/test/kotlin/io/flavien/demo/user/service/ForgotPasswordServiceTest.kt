package io.flavien.demo.user.service

import io.flavien.demo.config.MailProperties
import io.flavien.demo.user.repository.ForgotPasswordRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mail.javamail.JavaMailSender
import org.thymeleaf.TemplateEngine

@ExtendWith(MockitoExtension::class)
class ForgotPasswordServiceTest {

    @InjectMocks
    var rorgotPasswordService: ForgotPasswordService? = null

    @Mock
    var forgotPasswordRepository: ForgotPasswordRepository? = null

    @Mock
    var templateEngine: TemplateEngine? = null

    @Mock
    var emailSender: JavaMailSender? = null

    @Mock
    var mailProperties: MailProperties? = null

    // TODO: Implement tests
    @Test
    fun `Should send a forgotten password token by email`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should validate a forgotten password token`() {
        // Given

        // When

        // Then
    }

    // TODO: Implement tests
    @Test
    fun `Should delete forgotten password token by userId`() {
        // Given

        // When

        // Then
    }
}
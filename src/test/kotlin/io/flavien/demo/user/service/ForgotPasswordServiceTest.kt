package io.flavien.demo.user.service

import io.flavien.demo.config.MailProperties
import io.flavien.demo.core.util.RandomUtil
import io.flavien.demo.user.UserTestFactory
import io.flavien.demo.user.entity.ForgotPassword
import io.flavien.demo.user.exception.ChangePasswordFailedException
import io.flavien.demo.user.repository.ForgotPasswordRepository
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.*

@ExtendWith(MockitoExtension::class)
class ForgotPasswordServiceTest {

    @InjectMocks
    var forgotPasswordService: ForgotPasswordService? = null

    @Mock
    var forgotPasswordRepository: ForgotPasswordRepository? = null

    @Mock
    var templateEngine: TemplateEngine? = null

    @Mock
    var emailSender: JavaMailSender? = null

    @Mock
    var mailProperties: MailProperties? = null

    @Test
    fun `Should send a forgotten password token by email`() {
        // Given
        val user = UserTestFactory.initUser()
        user.id = 1L
        val token = "random-token"
        val domainLinks = "https://example.com"
        val accountCreator = "noreply@example.com"
        val emailContent = "Email content"

        mockkObject(RandomUtil)
        every { RandomUtil.randomString(64) } returns token

        `when`(forgotPasswordRepository!!.existsById(token)).thenReturn(false)
        `when`(mailProperties!!.domainLinks).thenReturn(domainLinks)
        `when`(mailProperties!!.accountCreator).thenReturn(accountCreator)
        `when`(templateEngine!!.process(Mockito.eq("forgot-password"), Mockito.any(Context::class.java))).thenReturn(emailContent)

        // When
        forgotPasswordService!!.sendForgotPasswordToken(user)

        // Then
        val forgotPasswordCaptor = ArgumentCaptor.forClass(ForgotPassword::class.java)
        verify(forgotPasswordRepository!!).save(forgotPasswordCaptor.capture())
        assertEquals(token, forgotPasswordCaptor.value.id)
        assertEquals(user.id, forgotPasswordCaptor.value.userId)

        val messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage::class.java)
        verify(emailSender!!).send(messageCaptor.capture())
        assertEquals(accountCreator, messageCaptor.value.from)
        assertEquals(user.email, messageCaptor.value.to!![0])
        assertEquals("Change password", messageCaptor.value.subject)
        assertEquals(emailContent, messageCaptor.value.text)

        // Clean up
        unmockkObject(RandomUtil)
    }

    @Test
    fun `Should validate a forgotten password token`() {
        // Given
        val token = "forgot-password-token"
        val userId = 1L
        val forgotPassword = UserTestFactory.initForgotPassword(token, userId)

        `when`(forgotPasswordRepository!!.findById(token)).thenReturn(Optional.of(forgotPassword))

        // When
        val result = forgotPasswordService!!.validate(token)

        // Then
        verify(forgotPasswordRepository!!).findById(token)
        verify(forgotPasswordRepository!!).delete(forgotPassword)
        assertEquals(forgotPassword, result)
    }

    @Test
    fun `Should throw ChangePasswordFailedException when token is invalid`() {
        // Given
        val token = "invalid-token"

        `when`(forgotPasswordRepository!!.findById(token)).thenReturn(Optional.empty())

        // When & Then
        assertThrows(ChangePasswordFailedException::class.java) {
            forgotPasswordService!!.validate(token)
        }

        verify(forgotPasswordRepository!!).findById(token)
    }

    @Test
    fun `Should delete forgotten password token by userId`() {
        // Given
        val userId = 1L

        // When
        forgotPasswordService!!.deleteByUserId(userId)

        // Then
        verify(forgotPasswordRepository!!).deleteByUserId(userId)
    }
}

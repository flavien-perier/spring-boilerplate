package io.flavien.demo.user.service

import io.flavien.demo.config.MailProperties
import io.flavien.demo.core.util.RandomUtil
import io.flavien.demo.user.UserTestFactory
import io.flavien.demo.user.entity.UserActivation
import io.flavien.demo.user.exception.ActivationFailedException
import io.flavien.demo.user.repository.UserActivationRepository
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

    @Test
    fun `Should send an activation token by email`() {
        // Given
        val user = UserTestFactory.initUser()
        user.id = 1L
        val token = "random-token"
        val domainLinks = "https://example.com"
        val accountCreator = "noreply@example.com"
        val emailContent = "Email content"

        mockkObject(RandomUtil)
        every { RandomUtil.randomString(64) } returns token

        `when`(userActivationRepository!!.existsById(token)).thenReturn(false)
        `when`(mailProperties!!.domainLinks).thenReturn(domainLinks)
        `when`(mailProperties!!.accountCreator).thenReturn(accountCreator)
        `when`(templateEngine!!.process(Mockito.eq("user-activation"), Mockito.any(Context::class.java))).thenReturn(emailContent)

        // When
        userActivationService!!.sendActivationToken(user)

        // Then
        val userActivationCaptor = ArgumentCaptor.forClass(UserActivation::class.java)
        verify(userActivationRepository!!).save(userActivationCaptor.capture())
        assertEquals(token, userActivationCaptor.value.id)
        assertEquals(user.id, userActivationCaptor.value.userId)

        val messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage::class.java)
        verify(emailSender!!).send(messageCaptor.capture())
        assertEquals(accountCreator, messageCaptor.value.from)
        assertEquals(user.email, messageCaptor.value.to!![0])
        assertEquals("Activation code", messageCaptor.value.subject)
        assertEquals(emailContent, messageCaptor.value.text)

        // Clean up
        unmockkObject(RandomUtil)
    }

    @Test
    fun `Should validate an activation token`() {
        // Given
        val token = "activation-token"
        val userId = 1L
        val userActivation = UserTestFactory.initUserActivation(token, userId)

        `when`(userActivationRepository!!.findById(token)).thenReturn(Optional.of(userActivation))

        // When
        val result = userActivationService!!.validate(token)

        // Then
        verify(userActivationRepository!!).findById(token)
        verify(userActivationRepository!!).delete(userActivation)
        assertEquals(userActivation, result)
    }

    @Test
    fun `Should throw ActivationFailedException when token is invalid`() {
        // Given
        val token = "invalid-token"

        `when`(userActivationRepository!!.findById(token)).thenReturn(Optional.empty())

        // When & Then
        assertThrows(ActivationFailedException::class.java) {
            userActivationService!!.validate(token)
        }

        verify(userActivationRepository!!).findById(token)
    }

    @Test
    fun `Should delete activation token by userId`() {
        // Given
        val userId = 1L

        // When
        userActivationService!!.deleteByUserId(userId)

        // Then
        verify(userActivationRepository!!).deleteByUserId(userId)
    }
}

package io.flavien.demo.domain.session.service

import io.flavien.demo.domain.configuration.properties.ApplicationProperties
import io.flavien.demo.domain.session.exception.BadPasswordFormatException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class PasswordServiceTest {
    private val passwordService = PasswordService(ApplicationProperties(minPasswordLength = 12))

    @ParameterizedTest
    @ValueSource(
        strings = [
            "Password123!",
            "Drowssap123!",
        ],
    )
    fun `should hash and verify password (round-trip)`(password: String) {
        // When
        val hash = passwordService.hashPassword(password, "salt")

        // Then
        assertThat(hash).startsWith("\$argon2")
        assertThat(passwordService.testPassword(password, "salt", hash)).isTrue()
        assertThat(passwordService.testPassword("Wrong-Password9!", "salt", hash)).isFalse()
        assertThat(passwordService.testPassword(password, "wrong-salt", hash)).isFalse()
    }

    @ParameterizedTest
    @CsvSource(
        "password123!", // No uppercase
        "PASSWORD123!", // No lowercase
        "Passwordaaa!", // No number
        "Password1234", // No special character
        "Aa1!", // Too short
    )
    fun `should not comply with all constraints`(password: String) {
        // When / Then
        assertThrows(BadPasswordFormatException::class.java) {
            passwordService.hashPassword(password, "salt")
        }
    }
}

package io.flavien.demo.domain.session.service

import io.flavien.demo.domain.configuration.properties.ApplicationProperties
import io.flavien.demo.domain.session.exception.BadPasswordFormatException
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.stereotype.Service

@Service
class PasswordService(
    private val applicationProperties: ApplicationProperties,
) {
    private val encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()

    fun hashPassword(
        password: String,
        salt: String,
    ): String {
        testPasswordFormat(password)

        return checkNotNull(encoder.encode("$password-$salt")) { "Argon2 encoder returned null" }
    }

    fun testPassword(
        password: String,
        salt: String,
        hash: String,
    ) = encoder.matches("$password-$salt", hash)

    private fun testPasswordFormat(password: String) {
        // Test password length
        if (password.length < applicationProperties.minPasswordLength) {
            throw BadPasswordFormatException()
        }

        // Password contains numbers
        if (password.matches(Regex(".*[0-9].*")).not()) {
            throw BadPasswordFormatException()
        }

        // Password contains at least one lowercase character
        if (password.matches(Regex(".*[a-z].*")).not()) {
            throw BadPasswordFormatException()
        }

        // Password contains at least one uppercase character
        if (password.matches(Regex(".*[A-Z].*")).not()) {
            throw BadPasswordFormatException()
        }

        // Password contains at least one special character
        if (password.matches(Regex(".*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")).not()) {
            throw BadPasswordFormatException()
        }
    }
}

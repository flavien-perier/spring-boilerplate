package io.flavien.demo.session.service

import io.flavien.demo.config.ApplicationProperties
import io.flavien.demo.session.exception.BadPasswordFormatException
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.security.MessageDigest

@Service
class PasswordService(
    private val applicationProperties: ApplicationProperties,
) {

    fun hashPassword(password: String, salt: String): String {
        testPasswordFormat(password)

        val md = MessageDigest.getInstance("SHA-512")
        val messageDigest = md.digest("$password-$salt".toByteArray())
        val no = BigInteger(1, messageDigest)
        return no.toString(16)
    }

    fun testPassword(password: String, salt: String, hash: String) = hashPassword(password, salt) == hash

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
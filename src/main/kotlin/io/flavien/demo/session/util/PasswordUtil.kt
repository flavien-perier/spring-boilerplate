package io.flavien.demo.session.util

import java.math.BigInteger
import java.security.MessageDigest

object PasswordUtil {

    fun hashPassword(password: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-512")
        val messageDigest = md.digest("$password-$salt".toByteArray())
        val no = BigInteger(1, messageDigest)
        return no.toString(16)
    }

    fun testPassword(password: String, salt: String, hash: String) = hashPassword(password, salt) == hash
}
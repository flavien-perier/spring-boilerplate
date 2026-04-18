package io.flavien.demo.domain.session.service

import org.springframework.stereotype.Service
import java.security.GeneralSecurityException
import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Service
class OtpService {
    companion object {
        private val BASE32_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray()
        private val VALID_BASE32_CHARS = BASE32_CHARS.toSet()
        private val BITS_LOOKUP =
            IntArray(128).apply {
                BASE32_CHARS.forEachIndexed { index, char ->
                    this[char.code] = index
                }
            }

        private const val HMAC_ALGO = "HmacSHA1"
        private const val TOTP_LENGTH = 6
        private const val TOTP_MODULUS = 1_000_000L
        private const val TIME_STEP = 30
    }

    fun encodeBase32(bytes: ByteArray): String {
        val encoded = StringBuilder()
        var buffer = 0L
        var bitsInBuffer = 0

        for (b in bytes) {
            buffer = (buffer shl 8) or (b.toLong() and 0xFF)
            bitsInBuffer += 8

            while (bitsInBuffer >= 5) {
                val index = ((buffer shr (bitsInBuffer - 5)) and 0x1F).toInt()
                encoded.append(BASE32_CHARS[index])
                bitsInBuffer -= 5
            }
        }

        if (bitsInBuffer > 0) {
            val index = ((buffer shl (5 - bitsInBuffer)) and 0x1F).toInt()
            encoded.append(BASE32_CHARS[index])
        }

        while (encoded.length % 8 != 0) {
            encoded.append('=')
        }

        return encoded.toString()
    }

    fun encodeBase32(input: String): String = encodeBase32(input.toByteArray(Charsets.UTF_8))

    fun generateSecret(): String {
        val bytes = ByteArray(20).apply { SecureRandom().nextBytes(this) }
        return encodeBase32(bytes)
    }

    fun decodeBase32ToByteArray(base32: String): ByteArray {
        val sanitizedBase32 = base32.uppercase().replace("=", "")
        val decoded = mutableListOf<Byte>()
        var buffer = 0L
        var bitsInBuffer = 0

        for (c in sanitizedBase32) {
            require(c.code < 128 && c in VALID_BASE32_CHARS) {
                "Invalid Base32 character: '$c'"
            }
            buffer = (buffer shl 5) or (BITS_LOOKUP[c.code].toLong())
            bitsInBuffer += 5

            while (bitsInBuffer >= 8) {
                val b = (buffer shr (bitsInBuffer - 8)).toByte()
                decoded.add(b)
                bitsInBuffer -= 8
            }
        }

        return decoded.toByteArray()
    }

    fun decodeBase32(base32: String): String = String(decodeBase32ToByteArray(base32), Charsets.UTF_8)

    fun generateTOTP(
        secretKey: String,
        timeInterval: Long,
    ): String {
        try {
            val decodedKey = decodeBase32ToByteArray(secretKey)
            val timeIntervalBytes = ByteArray(8)

            /*
             * In RFC 4226, it's specified that the counter value should be represented
             * in big-endian format when used as input for the HMAC computation.
             * Reference: https://datatracker.ietf.org/doc/html/rfc4226
             */
            var tempTimeInterval = timeInterval
            for (i in 7 downTo 0) {
                timeIntervalBytes[i] = (tempTimeInterval and 0xFFL).toByte()
                tempTimeInterval = tempTimeInterval shr 8
            }

            val hmac = Mac.getInstance(HMAC_ALGO)
            hmac.init(SecretKeySpec(decodedKey, HMAC_ALGO))
            val hash = hmac.doFinal(timeIntervalBytes)

            /*
             * Dynamic truncation per RFC 4226 section 5.3: extract a 4-byte substring
             * using an offset derived from the last nibble of the HMAC output.
             * The MSB is cleared (0x7F mask) to ensure the result is a positive integer.
             */
            val offset = hash[hash.size - 1].toInt() and 0xF

            val binaryCode =
                ((hash[offset].toInt() and 0x7F).toLong() shl 24) or
                    ((hash[offset + 1].toInt() and 0xFF).toLong() shl 16) or
                    ((hash[offset + 2].toInt() and 0xFF).toLong() shl 8) or
                    (hash[offset + 3].toInt() and 0xFF).toLong()

            return String.format("%0${TOTP_LENGTH}d", binaryCode % TOTP_MODULUS)
        } catch (e: GeneralSecurityException) {
            throw RuntimeException("Failed to generate TOTP", e)
        }
    }

    fun generateTOTP(secretKey: String): String {
        val timeInterval = System.currentTimeMillis() / 1000 / TIME_STEP
        return generateTOTP(secretKey, timeInterval)
    }

    fun validateTOTP(
        secretKey: String,
        inputTOTP: String,
    ): Boolean {
        val timeInterval = System.currentTimeMillis() / 1000 / TIME_STEP
        return (-1..1).any { i -> generateTOTP(secretKey, timeInterval + i) == inputTOTP }
    }
}

package io.flavien.demo.domain.shared.util

import java.security.SecureRandom
import java.util.UUID

object UuidUtil {
    private val secureRandom = SecureRandom()

    fun uuidv7(): UUID {
        val timestampMs = System.currentTimeMillis()
        val randomBytes = ByteArray(10)
        secureRandom.nextBytes(randomBytes)

        randomBytes[0] = ((randomBytes[0].toInt() and 0x0F) or 0x70).toByte()
        randomBytes[2] = ((randomBytes[2].toInt() and 0x3F) or 0x80).toByte()

        var msb = 0L
        for (i in 0..5) {
            msb = (msb shl 8) or ((timestampMs shr (40 - i * 8)) and 0xFF)
        }
        msb = (msb shl 8) or (randomBytes[0].toLong() and 0xFF)
        msb = (msb shl 8) or (randomBytes[1].toLong() and 0xFF)

        var lsb = 0L
        for (i in 2..9) {
            lsb = (lsb shl 8) or (randomBytes[i].toLong() and 0xFF)
        }

        return UUID(msb, lsb)
    }
}

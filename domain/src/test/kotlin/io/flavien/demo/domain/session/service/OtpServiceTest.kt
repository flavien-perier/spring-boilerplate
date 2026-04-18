package io.flavien.demo.domain.session.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class OtpServiceTest {
    private val otpService = OtpService()

    @Test
    fun `should roundtrip base32 encode and decode`() {
        // Given
        val input = "Hello World"

        // When
        val result = otpService.decodeBase32(otpService.encodeBase32(input))

        // Then
        assertThat(result).isEqualTo(input)
    }

    @Test
    fun `should encode empty string to empty string`() {
        // When
        val result = otpService.encodeBase32("")

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `should roundtrip string not aligned to 5-byte boundary`() {
        // Given
        val input = "a"

        // When
        val result = otpService.decodeBase32(otpService.encodeBase32(input))

        // Then
        assertThat(result).isEqualTo(input)
    }

    @Test
    fun `should decode known RFC 4648 base32 vector`() {
        // Given — RFC 4648 Section 10: BASE32("foobar") = "MZXW6YTBOI======"
        val encoded = "MZXW6YTBOI======"

        // When
        val result = otpService.decodeBase32(encoded)

        // Then
        assertThat(result).isEqualTo("foobar")
    }

    @Test
    fun `should reject invalid base32 character`() {
        // Given
        val invalidBase32 = "AAAA0AAA"

        // When / Then
        assertThatThrownBy { otpService.decodeBase32ToByteArray(invalidBase32) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("'0'")
    }

    @Test
    fun `should generate TOTP with correct format`() {
        // Given
        val secret = "JBSWY3DPEB3W64TMMQ======"

        // When
        val totp = otpService.generateTOTP(secret)

        // Then
        assertThat(totp).hasSize(6)
        assertThat(totp).containsOnlyDigits()
    }

    @Test
    fun `should generate correct TOTP for RFC 6238 test vector`() {
        // Given — secret "12345678901234567890" (RFC 6238 Appendix B), T=1
        // 8-digit TOTP = 94287082 (RFC 4226 Appendix D), so 6-digit = 287082
        val secret = "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ"

        // When
        val totp = otpService.generateTOTP(secret, 1L)

        // Then
        assertThat(totp).isEqualTo("287082")
    }

    @Test
    fun `should validate TOTP generated in current window`() {
        // Given
        val secret = "JBSWY3DPEB3W64TMMQ======"
        val totp = otpService.generateTOTP(secret)

        // When
        val result = otpService.validateTOTP(secret, totp)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `should reject invalid TOTP`() {
        // Given
        val secret = "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ"
        val validTotp = otpService.generateTOTP(secret, 1L)
        val wrongTotp = if (validTotp == "000000") "000001" else "000000"

        // When
        val result = otpService.validateTOTP(secret, wrongTotp)

        // Then
        assertThat(result).isFalse()
    }
}

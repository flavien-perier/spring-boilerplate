package io.flavien.demo.session.service

import io.flavien.demo.config.ApplicationProperties
import io.flavien.demo.session.exception.BadPasswordFormatException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PasswordServiceTest {

    @InjectMocks
    lateinit var passwordService: PasswordService

    @Mock
    lateinit var applicationProperties: ApplicationProperties

    @ParameterizedTest
    @CsvSource(
        "Password123!,salt1,3ec63f1ee6f073b892b24a5a6af0d13a4cbd2c44af62ff8851f820299e66242d75fddc0b3b17ef8018c9b26a71b7382156c3bfbe861bb505aca2a4aea530390b",
        "Drowssap123!,salt1,66d21616620df490eea6c4ad4fa86bdd5c6490fef546049a69ca5042da293d159d34b3b81c1da4251893abbe9a051aad62ec55cc1df311b66eea3fb8fe17bba4",
        "Drowssap123!,salt2,39feb93deac296f0075e517cc5eb5f6193bebe212669485bef9ca54aa445738c65fa7f623dab479fb376855908ca1e824c9f9117e318da1c35c7be02d543340f",
    )
    fun `should have the same hash`(password: String, salt: String, hash: String) {
        // Given
        `when`(applicationProperties.minPasswordLength).thenReturn(12)

        // When / Then
        assertThat(passwordService.hashPassword(password, salt))
            .isEqualTo(hash)

        verify(applicationProperties).minPasswordLength
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
        // Given
        `when`(applicationProperties.minPasswordLength).thenReturn(12)

        // When / Then
        assertThrows(BadPasswordFormatException::class.java) {
            passwordService.hashPassword(password, "salt")
        }

        verify(applicationProperties).minPasswordLength
    }
}
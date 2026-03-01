package io.flavien.demo.core.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class RandomUtilTest {

    @ParameterizedTest
    @CsvSource("5", "10", "20", "30", "40")
    fun `Test randomString`(length: Long) {
        // When
        val randomString = RandomUtil.randomString(length)

        // Then
        assertThat(randomString.length).isEqualTo(length)
        assertThat(randomString).matches("^[a-zA-Z0-9]+$")
    }
}
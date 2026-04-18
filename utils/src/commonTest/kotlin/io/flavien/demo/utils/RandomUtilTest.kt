package io.flavien.demo.utils

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RandomUtilTest {
    @Test
    fun `should return string of specified length`() {
        // Given
        val length = 10

        // When
        val result = RandomUtil.randomString(length)

        // Then
        assertEquals(length, result.length)
    }

    @Test
    fun `should contain only alphanumeric characters`() {
        // Given
        val length = 100
        val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        // When
        val result = RandomUtil.randomString(length)

        // Then
        result.forEach { char ->
            assertTrue(char in charPool, "Character $char is not in the expected pool")
        }
    }

    @Test
    fun `should generate different strings on multiple calls`() {
        // Given
        val seededRandom = Random(42)

        // When
        val strings = List(100) { RandomUtil.randomString(10, seededRandom) }

        // Then
        assertTrue(strings.toSet().size > 1, "Multiple calls should produce different strings")
    }
}

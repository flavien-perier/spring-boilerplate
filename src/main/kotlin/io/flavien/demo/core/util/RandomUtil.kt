package io.flavien.demo.core.util

import java.util.concurrent.ThreadLocalRandom
import kotlin.streams.asSequence

object RandomUtil {
    private val CHAR_POOL : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    fun randomString(length: Long) = ThreadLocalRandom.current()
        .ints(length, 0, CHAR_POOL.size)
        .asSequence()
        .map(CHAR_POOL::get)
        .joinToString("")
}
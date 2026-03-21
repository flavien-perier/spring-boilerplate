@file:OptIn(ExperimentalJsExport::class)

package io.flavien.demo.utils

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.random.Random

@JsExport
object RandomUtil {
    private val CHAR_POOL: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    fun randomString(length: Int) =
        buildString(length) {
            repeat(length) {
                append(CHAR_POOL[Random.nextInt(CHAR_POOL.size)])
            }
        }
}

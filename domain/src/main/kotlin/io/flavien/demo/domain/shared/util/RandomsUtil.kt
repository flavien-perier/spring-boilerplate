package io.flavien.demo.domain.shared.util

import java.security.SecureRandom
import kotlin.random.Random
import kotlin.random.asKotlinRandom

val SECURE_RANDOM: Random = SecureRandom().asKotlinRandom()

package io.flavien.demo.domain.session.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.io.Serializable

@RedisHash("OtpPending", timeToLive = 600)
data class OtpPending(
    @Id
    val userId: String,
    val secret: String,
) : Serializable

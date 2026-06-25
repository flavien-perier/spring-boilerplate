package io.flavien.demo.domain.user.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import java.io.Serializable

@RedisHash("ForgotPassword", timeToLive = 60 * 10)
class ForgotPassword(
    @Id
    val id: String,
    @Indexed
    val userId: String,
) : Serializable

package io.flavien.demo.user.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("ForgotPassword", timeToLive = 60 * 10)
class ForgotPassword(
    @Id
    val id: String,

    @Indexed
    val userId: Long,
)
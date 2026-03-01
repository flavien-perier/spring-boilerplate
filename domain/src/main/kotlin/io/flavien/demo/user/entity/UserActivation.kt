package io.flavien.demo.user.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("UserActivation", timeToLive = 60 * 5)
class UserActivation(
    @Id
    @Indexed
    val id: String,

    @Indexed
    val userId: Long,
)
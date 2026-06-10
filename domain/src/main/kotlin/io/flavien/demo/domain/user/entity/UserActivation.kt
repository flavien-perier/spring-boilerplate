package io.flavien.demo.domain.user.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import java.io.Serializable

@RedisHash("UserActivation", timeToLive = 60 * 5)
class UserActivation(
    @Id
    val id: String,
    @Indexed
    val userId: Long,
) : Serializable

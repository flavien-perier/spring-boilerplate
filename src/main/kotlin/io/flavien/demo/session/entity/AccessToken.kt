package io.flavien.demo.session.entity

import io.flavien.demo.user.model.UserRole
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import java.io.Serializable
import java.time.OffsetDateTime

@RedisHash("AccessToken", timeToLive = 900)
data class AccessToken(
    @Id
    @Indexed
    val id: String,

    @Indexed
    val userId: Long,

    val role: UserRole,

    val refreshTokenId: String,

    val creationDate: OffsetDateTime,
) : Serializable
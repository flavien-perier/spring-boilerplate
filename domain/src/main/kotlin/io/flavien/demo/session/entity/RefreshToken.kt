package io.flavien.demo.session.entity

import io.flavien.demo.user.model.UserRole
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import java.io.Serializable
import java.time.OffsetDateTime
import java.util.*

@RedisHash("RefreshToken", timeToLive = 3600 * 24 * 365)
data class RefreshToken(
    @Id
    @Indexed
    val id: String,

    @Indexed
    val uuid: UUID,

    @Indexed
    val userId: Long,

    val role: UserRole,

    val creationDate: OffsetDateTime,
) : Serializable
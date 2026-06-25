package io.flavien.demo.domain.session.entity

import io.flavien.demo.domain.session.model.REFRESH_TOKEN_TTL_SECONDS
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import java.io.Serializable
import java.time.OffsetDateTime
import java.util.UUID

@RedisHash("RefreshToken", timeToLive = REFRESH_TOKEN_TTL_SECONDS)
data class RefreshToken(
    @Id
    val id: String,
    @Indexed
    val uuid: UUID,
    @Indexed
    val userId: String,
    val creationDate: OffsetDateTime,
) : Serializable

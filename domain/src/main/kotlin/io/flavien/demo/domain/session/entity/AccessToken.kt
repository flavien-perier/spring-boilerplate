package io.flavien.demo.domain.session.entity

import io.flavien.demo.domain.permission.model.PermissionEnum
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import java.io.Serializable
import java.time.OffsetDateTime

@RedisHash("AccessToken", timeToLive = 900)
data class AccessToken(
    @Id
    val id: String,
    @Indexed
    val userId: String,
    val refreshTokenId: String,
    val creationDate: OffsetDateTime,
    val permissions: Set<PermissionEnum> = emptySet(),
) : Serializable

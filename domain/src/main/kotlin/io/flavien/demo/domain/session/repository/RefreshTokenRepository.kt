package io.flavien.demo.domain.session.repository

import io.flavien.demo.domain.session.entity.RefreshToken
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RefreshTokenRepository : CrudRepository<RefreshToken, String> {
    fun findByUserId(userId: Long): List<RefreshToken>

    fun getByUuid(uuid: UUID): RefreshToken?

    fun deleteByUserId(userId: Long)
}

package io.flavien.demo.domain.session.service

import io.flavien.demo.domain.session.entity.RefreshToken
import io.flavien.demo.domain.session.exception.BadRefreshTokenException
import io.flavien.demo.domain.session.repository.RefreshTokenRepository
import io.flavien.demo.domain.shared.util.SECURE_RANDOM
import io.flavien.demo.library.common.RandomUtil
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.Collections
import java.util.UUID

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    fun create(userId: UUID): RefreshToken {
        val id = RandomUtil.randomString(64, SECURE_RANDOM)

        val refreshToken = RefreshToken(id, UUID.randomUUID(), userId.toString(), OffsetDateTime.now())
        refreshTokenRepository.save(refreshToken)

        return refreshToken
    }

    fun get(token: String): RefreshToken = refreshTokenRepository.findById(token).orElseThrow { BadRefreshTokenException() }

    fun get(uuid: UUID) = refreshTokenRepository.getByUuid(uuid) ?: throw BadRefreshTokenException()

    fun findByUserId(userId: UUID) = refreshTokenRepository.findByUserId(userId.toString()).sortedByDescending { it.creationDate }

    fun findByUserId(
        userId: UUID,
        pageable: Pageable,
    ): Page<RefreshToken> {
        val all = refreshTokenRepository.findByUserId(userId.toString())

        val sorted =
            if (pageable.sort.isSorted) {
                all.sortedWith(buildComparator(pageable.sort))
            } else {
                all.sortedByDescending { it.creationDate }
            }

        val total = sorted.size.toLong()
        val from = pageable.offset.toInt().coerceAtMost(sorted.size)
        val to = (from + pageable.pageSize).coerceAtMost(sorted.size)
        val content = if (from < to) sorted.subList(from, to) else emptyList()

        return PageImpl(content, pageable, total)
    }

    private fun buildComparator(sort: Sort): Comparator<RefreshToken> {
        var comparator: Comparator<RefreshToken>? = null
        for (order in sort) {
            val propertyComparator: Comparator<RefreshToken> =
                when (order.property) {
                    "uuid" -> compareBy { it.uuid }
                    "creationDate" -> compareBy { it.creationDate }
                    "userId" -> compareBy { it.userId }
                    "id" -> compareBy { it.id }
                    else -> compareBy { it.creationDate }
                }
            val directed =
                if (order.isAscending) {
                    propertyComparator
                } else {
                    Collections.reverseOrder(propertyComparator)
                }
            comparator = comparator?.then(directed) ?: directed
        }
        return comparator ?: compareByDescending { it.creationDate }
    }

    fun exists(token: String) = refreshTokenRepository.existsById(token)

    fun delete(token: String) = refreshTokenRepository.deleteById(token)

    fun delete(
        uuid: UUID,
        userId: UUID,
    ) {
        val refreshToken = get(uuid)
        if (refreshToken.userId != userId.toString()) {
            throw BadRefreshTokenException()
        }
        refreshTokenRepository.delete(refreshToken)
    }

    fun deleteByUserId(userId: UUID) = refreshTokenRepository.deleteByUserId(userId.toString())
}

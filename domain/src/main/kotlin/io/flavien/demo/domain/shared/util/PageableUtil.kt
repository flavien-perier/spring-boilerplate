package io.flavien.demo.domain.shared.util

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

object PageableUtil {
    fun toPageable(
        page: Int?,
        pageSize: Int?,
        sortColumn: String?,
        sortOrder: String?,
        defaultSortColumn: String,
    ): Pageable {
        val safePage = ((page ?: 1) - 1).coerceAtLeast(0)
        val safeSize = (pageSize ?: 10).coerceAtLeast(1)
        val direction = Sort.Direction.fromString(sortOrder ?: "ASC")
        return PageRequest.of(safePage, safeSize, Sort.by(direction, sortColumn ?: defaultSortColumn))
    }
}

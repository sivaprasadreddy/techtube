package com.sivalabs.techtube.common.models

import org.springframework.data.domain.Page

data class PagedResult<T>(
    val data: Iterable<T>,
    val totalElements: Long,
    val pageNumber: Int,
    val totalPages: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
    val hasNext: Boolean,
    val hasPrevious: Boolean,
) {
    companion object {
        fun <T> of(page: Page<T>): PagedResult<T> =
            PagedResult(
                data = page.content,
                totalElements = page.totalElements,
                pageNumber = page.number + 1, // 1 - based index
                totalPages = page.totalPages,
                isFirst = page.isFirst,
                isLast = page.isLast,
                hasNext = page.hasNext(),
                hasPrevious = page.hasPrevious(),
            )
    }
}

package com.sivalabs.techtube.common.models

data class PagedResult<T>(
    val data: Iterable<T>,
    val totalElements: Long,
    val pageNumber: Int,
    val totalPages: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
    val hasNext: Boolean,
    val hasPrevious: Boolean,
)

package com.sivalabs.techtube.videos.domain

import com.sivalabs.techtube.users.domain.UserDTO
import java.time.LocalDateTime

data class VideoDTO(
    val id: Long,
    val title: String,
    val url: String,
    val description: String,
    val category: CategoryDTO,
    val createdBy: UserDTO,
    val status: VideoStatus,
    val createdAt: LocalDateTime,
    var favorited: Boolean = false,
)

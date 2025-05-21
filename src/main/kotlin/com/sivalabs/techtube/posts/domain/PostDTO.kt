package com.sivalabs.techtube.posts.domain

import com.sivalabs.techtube.users.domain.UserDTO
import java.time.LocalDateTime

data class PostDTO(
    val id: Long,
    val title: String,
    val url: String,
    val description: String,
    val category: CategoryDTO,
    val createdBy: UserDTO,
    val status: PostStatus,
    val createdAt: LocalDateTime,
    var favorited: Boolean = false,
)

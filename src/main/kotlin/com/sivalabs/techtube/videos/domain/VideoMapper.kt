package com.sivalabs.techtube.videos.domain

import com.sivalabs.techtube.users.domain.User
import com.sivalabs.techtube.users.domain.UserDTO
import org.springframework.stereotype.Component

@Component
class VideoMapper {
    fun toDTO(video: Video): VideoDTO =
        VideoDTO(
            id = video.id ?: 0,
            title = video.title,
            url = video.url,
            description = video.description,
            category = toCategoryDTO(video.category!!),
            createdBy = toUserDTO(video.createdBy!!),
            status = video.status,
            createdAt = video.createdAt,
            favorited = false,
        )

    private fun toCategoryDTO(category: Category): CategoryDTO =
        CategoryDTO(
            id = category.id ?: 0,
            name = category.name,
        )

    private fun toUserDTO(user: User): UserDTO =
        UserDTO(
            id = user.id ?: 0,
            name = user.name,
            email = user.email,
            role = user.role,
        )
}

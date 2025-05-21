package com.sivalabs.techtube.posts.domain

import com.sivalabs.techtube.users.domain.User
import com.sivalabs.techtube.users.domain.UserDTO
import org.springframework.stereotype.Component

@Component
class PostMapper(
    private val favoriteRepository: FavoriteRepository,
) {
    fun toDTO(
        post: Post,
        currentUser: User? = null,
    ): PostDTO =
        PostDTO(
            id = post.id ?: 0,
            title = post.title,
            url = post.url,
            description = post.description,
            category = toCategoryDTO(post.category!!),
            createdBy = toUserDTO(post.createdBy!!),
            status = post.status,
            createdAt = post.createdAt,
            favorited =
                currentUser?.let { user ->
                    favoriteRepository.existsByUserAndPost(user, post)
                } ?: false,
        )

    fun toDTOList(
        posts: List<Post>,
        currentUser: User? = null,
    ): List<PostDTO> = posts.map { toDTO(it, currentUser) }

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

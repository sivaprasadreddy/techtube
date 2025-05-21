package com.sivalabs.techtube.posts.domain

import com.sivalabs.techtube.users.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface FavoriteRepository : JpaRepository<Favorite, Long> {
    fun existsByUserAndPost(
        user: User,
        post: Post,
    ): Boolean

    fun deleteByUserAndPost(
        user: User,
        post: Post,
    )
}

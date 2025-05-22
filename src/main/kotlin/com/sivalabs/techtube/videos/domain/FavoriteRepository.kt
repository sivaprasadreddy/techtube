package com.sivalabs.techtube.videos.domain

import com.sivalabs.techtube.users.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface FavoriteRepository : JpaRepository<Favorite, Long> {
    fun existsByUserAndVideo(
        user: User,
        video: Video,
    ): Boolean

    fun deleteByUserAndVideo(
        user: User,
        video: Video,
    )
}

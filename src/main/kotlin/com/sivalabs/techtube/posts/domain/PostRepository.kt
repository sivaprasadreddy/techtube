package com.sivalabs.techtube.posts.domain

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PostRepository : JpaRepository<Post, Long> {
    @Query(
        """
        select p from Post p join fetch p.category join fetch p.createdBy
    """,
    )
    fun findAllPosts(pageable: Pageable): Page<Post>
}

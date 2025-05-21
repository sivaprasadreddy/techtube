package com.sivalabs.techtube.posts.domain

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PostRepository : JpaRepository<Post, Long> {
    @Query(
        """
        select p from Post p join fetch p.category join fetch p.createdBy
        where p.status = com.sivalabs.techtube.posts.domain.PostStatus.APPROVED
    """,
    )
    fun findAllPosts(pageable: Pageable): Page<Post>

    @Query(
        """
        select p from Post p join fetch p.category join fetch p.createdBy
        where p.status = com.sivalabs.techtube.posts.domain.PostStatus.APPROVED
        and (:categoryId is null or p.category.id = :categoryId)
        and (:searchTerm is null or lower(p.title) like lower(concat('%', :searchTerm, '%'))
             or lower(p.description) like lower(concat('%', :searchTerm, '%')))
    """,
    )
    fun findPostsByCategoryAndSearchTerm(
        categoryId: Long?,
        searchTerm: String?,
        pageable: Pageable,
    ): Page<Post>

    @Query(
        """
        select p from Post p join fetch p.category join fetch p.createdBy
        where p.status = com.sivalabs.techtube.posts.domain.PostStatus.PENDING
    """,
    )
    fun findAllPendingPosts(): List<Post>
}

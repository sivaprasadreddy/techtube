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
        where p.status = :status
    """,
    )
    fun findAllPostsByStatus(status: PostStatus): List<Post>

    @Query(
        """
        select p from Post p join fetch p.category join fetch p.createdBy
        where p.createdBy.id = :userId
        order by p.createdAt desc
    """,
    )
    fun findPostsByUserId(userId: Long): List<Post>

    @Query(
        """
        select f.post.id from Favorite f
        where f.post.id in :postIds and f.user.id = :userId
    """,
    )
    fun findFavouritePostIds(
        userId: Long,
        postIds: List<Long>,
    ): List<Long>
}

package com.sivalabs.techtube.videos.domain

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface VideoRepository : JpaRepository<Video, Long> {
    @Query(
        """
        select v from Video v join fetch v.category join fetch v.createdBy
        where v.status = com.sivalabs.techtube.videos.domain.VideoStatus.APPROVED
    """,
    )
    fun findAllVideos(pageable: Pageable): Page<Video>

    @Query(
        """
        select v from Video v join fetch v.category join fetch v.createdBy
        where v.status = com.sivalabs.techtube.videos.domain.VideoStatus.APPROVED
        and (:categoryId is null or v.category.id = :categoryId)
        and (:searchTerm is null or lower(v.title) like lower(concat('%', :searchTerm, '%'))
             or lower(v.description) like lower(concat('%', :searchTerm, '%')))
    """,
    )
    fun findVideosByCategoryAndSearchTerm(
        categoryId: Long?,
        searchTerm: String?,
        pageable: Pageable,
    ): Page<Video>

    @Query(
        """
        select v from Video v join fetch v.category join fetch v.createdBy
        where v.status = :status
    """,
    )
    fun findAllVideosByStatus(status: VideoStatus): List<Video>

    @Query(
        """
        select v from Video v join fetch v.category join fetch v.createdBy
        where v.createdBy.id = :userId
        order by v.createdAt desc
    """,
    )
    fun findVideosByUserId(userId: Long): List<Video>

    @Query(
        """
        select v from Video v join fetch v.category join fetch v.createdBy
        where v.id in (select f.video.id from Favorite f where f.user.id = :userId)
        order by v.createdAt desc
    """,
    )
    fun findFavoritesByUserId(userId: Long): List<Video>

    @Query(
        """
        select f.video.id from Favorite f
        where f.video.id in :videoIds and f.user.id = :userId
    """,
    )
    fun findFavouriteVideoIds(
        userId: Long,
        videoIds: List<Long>,
    ): List<Long>
}

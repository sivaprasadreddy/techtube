package com.sivalabs.techtube.videos.domain

import com.sivalabs.techtube.common.models.PagedResult
import com.sivalabs.techtube.users.domain.User
import com.sivalabs.techtube.users.domain.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Service
class VideoService(
    private val videoRepository: VideoRepository,
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository,
    private val favoriteRepository: FavoriteRepository,
    private val videoMapper: VideoMapper,
) {
    @Transactional(readOnly = true)
    fun getVideos(
        page: Int,
        categoryId: Long? = null,
        searchTerm: String? = null,
        sortBy: String = "createdAt",
        sortDirection: String = "desc",
        currentUser: User? = null,
    ): PagedResult<VideoDTO> {
        val direction = if (sortDirection.equals("asc", ignoreCase = true)) Sort.Direction.ASC else Sort.Direction.DESC
        val pageable = PageRequest.of(page - 1, 6, Sort.by(direction, sortBy))

        val videosPage =
            if (categoryId != null || !searchTerm.isNullOrBlank()) {
                videoRepository.findVideosByCategoryAndSearchTerm(categoryId, searchTerm, pageable)
            } else {
                videoRepository.findAllVideos(pageable)
            }

        if (currentUser == null) {
            return PagedResult.of(videosPage.map { video -> videoMapper.toDTO(video) })
        }
        val videoIds = videosPage.content.map { it.id!! }
        val favouritedVideoIds = videoRepository.findFavouriteVideoIds(currentUser.id!!, videoIds)
        val videoDTOsPage = videosPage.map { video -> mapToVideoDTO(video, favouritedVideoIds) }
        return PagedResult.of(videoDTOsPage)
    }

    private fun mapToVideoDTO(
        video: Video,
        favouritedVideoIds: List<Long>,
    ): VideoDTO {
        val dto = videoMapper.toDTO(video)
        dto.favorited = video.id in favouritedVideoIds
        return dto
    }

    @Transactional
    fun createVideo(cmd: CreateVideoCmd): Long {
        val user = findUserById(cmd.userId)
        val category = findCategoryById(cmd.categoryId)

        val video =
            Video().apply {
                title = cmd.title
                url = cmd.url
                description = cmd.description
                this.category = category
                createdBy = user
                status = VideoStatus.PENDING
            }

        videoRepository.save(video)
        return video.id!!
    }

    @Transactional(readOnly = true)
    fun getPublishedVideos(): List<VideoDTO> =
        videoRepository
            .findAllVideosByStatus(VideoStatus.APPROVED)
            .map(videoMapper::toDTO)

    @Transactional(readOnly = true)
    fun getPendingVideos(): List<VideoDTO> =
        videoRepository
            .findAllVideosByStatus(VideoStatus.PENDING)
            .map(videoMapper::toDTO)

    @Transactional(readOnly = true)
    fun getRejectedVideos(): List<VideoDTO> =
        videoRepository
            .findAllVideosByStatus(VideoStatus.REJECTED)
            .map(videoMapper::toDTO)

    @Transactional
    fun approveVideo(videoId: Long) = updateVideoStatus(videoId, VideoStatus.APPROVED)

    @Transactional
    fun rejectVideo(videoId: Long) = updateVideoStatus(videoId, VideoStatus.REJECTED)

    @Transactional
    fun unpublishVideo(videoId: Long) = updateVideoStatus(videoId, VideoStatus.PENDING)

    private fun updateVideoStatus(
        videoId: Long,
        status: VideoStatus,
    ) {
        val video = findVideoById(videoId)
        video.status = status
        videoRepository.save(video)
    }

    @Transactional
    fun deleteVideo(videoId: Long) {
        videoRepository.deleteById(videoId)
    }

    @Transactional(readOnly = true)
    fun getVideosByUser(userId: Long): List<Video> = videoRepository.findVideosByUserId(userId)

    fun getVideoById(id: Long): Optional<VideoDTO> =
        videoRepository
            .findById(id)
            .map(videoMapper::toDTO)

    @Transactional
    fun favoriteVideo(
        videoId: Long,
        userId: Long,
    ): Boolean {
        val video = findVideoById(videoId)
        val user = findUserById(userId)

        if (favoriteRepository.existsByUserAndVideo(user, video)) {
            return true // Already favorited
        }

        val favorite =
            Favorite().apply {
                this.user = user
                this.video = video
            }
        favoriteRepository.save(favorite)
        return true
    }

    @Transactional
    fun unfavoriteVideo(
        videoId: Long,
        userId: Long,
    ): Boolean {
        val video = findVideoById(videoId)
        val user = findUserById(userId)

        if (!favoriteRepository.existsByUserAndVideo(user, video)) {
            return true // Already not favorited
        }

        favoriteRepository.deleteByUserAndVideo(user, video)
        return true
    }

    // Helper methods to reduce code duplication
    private fun findVideoById(videoId: Long): Video =
        videoRepository
            .findById(videoId)
            .orElseThrow { IllegalArgumentException("Video not found with id: $videoId") }

    private fun findUserById(userId: Long): User =
        userRepository
            .findById(userId)
            .orElseThrow { IllegalArgumentException("User not found with id: $userId") }

    private fun findCategoryById(categoryId: Long): Category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow { IllegalArgumentException("Category not found with id: $categoryId") }
}

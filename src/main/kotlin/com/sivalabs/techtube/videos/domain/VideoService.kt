package com.sivalabs.techtube.videos.domain

import com.sivalabs.techtube.common.models.PagedResult
import com.sivalabs.techtube.users.domain.User
import com.sivalabs.techtube.users.domain.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import kotlin.collections.contains
import kotlin.collections.map

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
        val videoIds = videosPage.content.map { video -> video.id!! }
        val favouritedVideoIds = videoRepository.findFavouriteVideoIds(currentUser.id!!, videoIds)
        val videoDTOsPage = videosPage.map { video -> mapToVideoDTO(video, favouritedVideoIds) }
        return PagedResult.of(videoDTOsPage)
    }

    private fun mapToVideoDTO(
        video: Video,
        favouritedVideoIds: List<Long>,
    ): VideoDTO {
        val dto = videoMapper.toDTO(video)
        dto.favorited = favouritedVideoIds.contains(video.id)
        return dto
    }

    @Transactional
    fun createVideo(cmd: CreateVideoCmd): Long {
        val user = userRepository.findById(cmd.userId).orElseThrow { IllegalStateException("User not found") }
        val category = categoryRepository.findById(cmd.categoryId).orElseThrow { IllegalArgumentException("Category not found") }

        val video = Video()
        video.title = cmd.title
        video.url = cmd.url
        video.description = cmd.description
        video.category = category
        video.createdBy = user
        video.status = VideoStatus.PENDING

        videoRepository.save(video)
        return video.id!!
    }

    @Transactional(readOnly = true)
    fun getPublishedVideos(): List<VideoDTO> = videoRepository.findAllVideosByStatus(VideoStatus.APPROVED).map { videoMapper.toDTO(it) }

    @Transactional(readOnly = true)
    fun getPendingVideos(): List<VideoDTO> = videoRepository.findAllVideosByStatus(VideoStatus.PENDING).map { videoMapper.toDTO(it) }

    @Transactional(readOnly = true)
    fun getRejectedVideos(): List<VideoDTO> = videoRepository.findAllVideosByStatus(VideoStatus.REJECTED).map { videoMapper.toDTO(it) }

    @Transactional
    fun approveVideo(videoId: Long) {
        val video =
            videoRepository
                .findById(videoId)
                .orElseThrow { IllegalArgumentException("Video not found with id: $videoId") }
        video.status = VideoStatus.APPROVED
        videoRepository.save(video)
    }

    @Transactional
    fun rejectVideo(videoId: Long) {
        val video =
            videoRepository
                .findById(videoId)
                .orElseThrow { IllegalArgumentException("Video not found with id: $videoId") }
        video.status = VideoStatus.REJECTED
        videoRepository.save(video)
    }

    @Transactional
    fun unpublishVideo(videoId: Long) {
        val video =
            videoRepository
                .findById(videoId)
                .orElseThrow { IllegalArgumentException("Video not found with id: $videoId") }
        video.status = VideoStatus.PENDING
        videoRepository.save(video)
    }

    @Transactional
    fun deleteVideo(videoId: Long) {
        videoRepository.deleteById(videoId)
    }

    @Transactional(readOnly = true)
    fun getVideosByUser(userId: Long): List<Video> = videoRepository.findVideosByUserId(userId)

    fun getVideoById(id: Long): Optional<VideoDTO> = videoRepository.findById(id).map { videoMapper.toDTO(it) }

    @Transactional
    fun favoriteVideo(
        videoId: Long,
        userId: Long,
    ): Boolean {
        val video =
            videoRepository
                .findById(videoId)
                .orElseThrow { IllegalArgumentException("Video not found with id: $videoId") }
        val user =
            userRepository
                .findById(userId)
                .orElseThrow { IllegalArgumentException("User not found with id: $userId") }

        if (favoriteRepository.existsByUserAndVideo(user, video)) {
            return true // Already favorited
        }

        val favorite = Favorite()
        favorite.user = user
        favorite.video = video
        favoriteRepository.save(favorite)
        return true
    }

    @Transactional
    fun unfavoriteVideo(
        videoId: Long,
        userId: Long,
    ): Boolean {
        val video =
            videoRepository
                .findById(videoId)
                .orElseThrow { IllegalArgumentException("Video not found with id: $videoId") }
        val user =
            userRepository
                .findById(userId)
                .orElseThrow { IllegalArgumentException("User not found with id: $userId") }

        if (!favoriteRepository.existsByUserAndVideo(user, video)) {
            return true // Already not favorited
        }

        favoriteRepository.deleteByUserAndVideo(user, video)
        return true
    }
}

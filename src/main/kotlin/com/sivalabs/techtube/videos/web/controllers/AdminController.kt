package com.sivalabs.techtube.videos.web.controllers

import com.sivalabs.techtube.videos.domain.VideoService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/admin")
class AdminController(
    private val videoService: VideoService,
) {
    companion object {
        private const val REVIEW_VIDEOS_VIEW = "admin/review-videos"
        private const val REDIRECT_REVIEW_VIDEOS = "redirect:/admin/review-videos"
        private const val SUCCESS_MESSAGE = "successMessage"
        private const val ERROR_MESSAGE = "errorMessage"
    }

    @GetMapping("/review-videos")
    fun showVideosToReview(model: Model): String {
        with(model) {
            this["publishedVideos"] = videoService.getPublishedVideos()
            this["pendingVideos"] = videoService.getPendingVideos()
            this["rejectedVideos"] = videoService.getRejectedVideos()
        }
        return REVIEW_VIDEOS_VIEW
    }

    @PostMapping("/videos/{id}/approve")
    fun approveVideo(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ): String = performVideoAction(id, redirectAttributes, "approved") { videoService.approveVideo(it) }

    @PostMapping("/videos/{id}/reject")
    fun rejectVideo(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ): String = performVideoAction(id, redirectAttributes, "rejected") { videoService.rejectVideo(it) }

    @PostMapping("/videos/{id}/unpublish")
    fun unpublishVideo(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ): String = performVideoAction(id, redirectAttributes, "unpublished") { videoService.unpublishVideo(it) }

    @PostMapping("/videos/{id}/delete")
    fun deleteVideo(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ): String = performVideoAction(id, redirectAttributes, "deleted") { videoService.deleteVideo(it) }

    private inline fun performVideoAction(
        id: Long,
        redirectAttributes: RedirectAttributes,
        action: String,
        operation: (Long) -> Unit,
    ): String =
        try {
            operation(id)
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Video has been $action successfully.")
            REDIRECT_REVIEW_VIDEOS
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute(
                ERROR_MESSAGE,
                e.message ?: "An error occurred while ${action.removeSuffix("ed")}ing the video.",
            )
            REDIRECT_REVIEW_VIDEOS
        }
}

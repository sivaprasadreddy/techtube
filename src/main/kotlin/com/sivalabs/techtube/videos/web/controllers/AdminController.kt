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
    @GetMapping("/review-videos")
    fun showVideosToReview(model: Model): String {
        model["publishedVideos"] = videoService.getPublishedVideos()
        model["pendingVideos"] = videoService.getPendingVideos()
        model["rejectedVideos"] = videoService.getRejectedVideos()
        return "admin/review-videos"
    }

    @PostMapping("/videos/{id}/approve")
    fun approveVideo(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ): String {
        try {
            videoService.approveVideo(id)
            redirectAttributes.addFlashAttribute("successMessage", "Video has been approved successfully.")
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                e.message ?: "An error occurred while approving the video.",
            )
        }
        return "redirect:/admin/review-videos"
    }

    @PostMapping("/videos/{id}/reject")
    fun rejectVideo(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ): String {
        try {
            videoService.rejectVideo(id)
            redirectAttributes.addFlashAttribute("successMessage", "Video has been rejected successfully.")
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                e.message ?: "An error occurred while rejecting the video.",
            )
        }
        return "redirect:/admin/review-videos"
    }

    @PostMapping("/videos/{id}/unpublish")
    fun unpublishVideo(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ): String {
        try {
            videoService.unpublishVideo(id)
            redirectAttributes.addFlashAttribute("successMessage", "Video has been unpublished successfully.")
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                e.message ?: "An error occurred while unpublishing the video.",
            )
        }
        return "redirect:/admin/review-videos"
    }

    @PostMapping("/videos/{id}/delete")
    fun deleteVideo(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ): String {
        try {
            videoService.deleteVideo(id)
            redirectAttributes.addFlashAttribute("successMessage", "Video has been deleted successfully.")
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                e.message ?: "An error occurred while deleting the video.",
            )
        }
        return "redirect:/admin/review-videos"
    }
}

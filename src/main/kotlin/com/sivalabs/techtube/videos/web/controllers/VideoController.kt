package com.sivalabs.techtube.videos.web.controllers

import com.sivalabs.techtube.users.domain.SecurityService
import com.sivalabs.techtube.users.domain.UserRepository
import com.sivalabs.techtube.videos.domain.CategoryService
import com.sivalabs.techtube.videos.domain.CreateVideoCmd
import com.sivalabs.techtube.videos.domain.VideoService
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.View
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.springframework.web.servlet.view.FragmentsRendering

@Controller
class VideoController(
    private val videoService: VideoService,
    private val categoryService: CategoryService,
    private val securityService: SecurityService,
    private val userRepository: UserRepository,
) {
    @GetMapping("/")
    fun home() = "redirect:/videos"

    @GetMapping("/videos")
    fun showVideos(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) searchTerm: String?,
        @RequestParam(defaultValue = "createdAt") sortBy: String,
        @RequestParam(defaultValue = "desc") sortDirection: String,
        model: Model,
    ): String {
        val currentUser =
            securityService.getLoginUserId()?.let { userId ->
                userRepository.findById(userId).orElse(null)
            }
        val videos = videoService.getVideos(page, categoryId, searchTerm, sortBy, sortDirection, currentUser)
        val categories = categoryService.getAllCategories()

        model["videos"] = videos
        model["categories"] = categories
        model["selectedCategoryId"] = categoryId
        model["searchTerm"] = searchTerm ?: ""
        model["sortBy"] = sortBy
        model["sortDirection"] = sortDirection

        return "videos/videos"
    }

    @GetMapping("/videos/new")
    fun showCreateVideoForm(model: Model): String {
        val categories = categoryService.getAllCategories()
        model["categories"] = categories
        model["videoForm"] = CreateVideoForm()
        return "videos/create-video"
    }

    @PostMapping("/videos")
    fun createVideo(
        @Valid @ModelAttribute("videoForm") form: CreateVideoForm,
        bindingResult: BindingResult,
        model: Model,
        redirectAttributes: RedirectAttributes,
    ): String {
        if (bindingResult.hasErrors()) {
            model["categories"] = categoryService.getAllCategories()
            return "videos/create-video"
        }

        try {
            val userId = securityService.getLoginUserIdOrThrow()

            val cmd =
                CreateVideoCmd(
                    title = form.title,
                    url = form.url,
                    description = form.description,
                    categoryId = form.categoryId!!,
                    userId = userId,
                )
            videoService.createVideo(cmd)
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Your video has been submitted and is pending review",
            )
            return "redirect:/videos"
        } catch (e: Exception) {
            model["errorMessage"] = e.message ?: "An error occurred while submitting your tutorial."
            model["categories"] = categoryService.getAllCategories()
            return "videos/create-video"
        }
    }

    class CreateVideoForm(
        @field:NotBlank(message = "Title is required")
        var title: String = "",
        @field:NotBlank(message = "URL is required")
        var url: String = "",
        @field:NotBlank(message = "Description is required")
        var description: String = "",
        @field:NotNull(message = "Category is required")
        var categoryId: Long? = null,
    )

    @GetMapping("/videos/my-videos")
    fun showMyVideos(model: Model): String {
        try {
            val userId = securityService.getLoginUserIdOrThrow()
            val videos = videoService.getVideosByUser(userId)
            model["videos"] = videos
        } catch (e: Exception) {
            model["errorMessage"] = e.message ?: "An error occurred while retrieving your videos."
        }
        return "videos/my-videos"
    }

    @GetMapping("/admin/review-videos")
    fun showVideosToReview(model: Model): String {
        model["publishedVideos"] = videoService.getPublishedVideos()
        model["pendingVideos"] = videoService.getPendingVideos()
        model["rejectedVideos"] = videoService.getRejectedVideos()
        return "admin/review-videos"
    }

    @PostMapping("/admin/videos/{id}/approve")
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

    @PostMapping("/admin/videos/{id}/reject")
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

    @PostMapping("/admin/videos/{id}/unpublish")
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

    @PostMapping("/admin/videos/{id}/delete")
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

    @PostMapping("/videos/{id}/favorite")
    @HxRequest
    fun favoriteVideo(
        @PathVariable id: Long,
    ): View {
        val userId = securityService.getLoginUserIdOrThrow()
        videoService.favoriteVideo(id, userId)
        val video = videoService.getVideoById(id).orElseThrow { IllegalStateException("Video not found") }
        return FragmentsRendering.with("partials/unfavourite-video", mapOf("video" to video)).build()
    }

    @PostMapping("/videos/{id}/unfavorite")
    @HxRequest
    fun unfavoriteVideo(
        @PathVariable id: Long,
    ): View {
        val userId = securityService.getLoginUserIdOrThrow()
        videoService.unfavoriteVideo(id, userId)
        val video = videoService.getVideoById(id).orElseThrow { IllegalStateException("Video not found") }
        return FragmentsRendering.with("partials/favourite-video", mapOf("video" to video)).build()
    }
}

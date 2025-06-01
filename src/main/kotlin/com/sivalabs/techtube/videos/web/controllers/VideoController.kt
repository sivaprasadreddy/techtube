package com.sivalabs.techtube.videos.web.controllers

import com.sivalabs.techtube.users.domain.SecurityService
import com.sivalabs.techtube.users.domain.UserRepository
import com.sivalabs.techtube.videos.domain.CategoryService
import com.sivalabs.techtube.videos.domain.CreateVideoCmd
import com.sivalabs.techtube.videos.domain.VideoService
import com.sivalabs.techtube.videos.domain.YouTubeApi
import com.sivalabs.techtube.videos.web.dto.CreateVideoForm
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import jakarta.validation.Valid
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
    private val youTubeApi: YouTubeApi,
    private val securityService: SecurityService,
    private val userRepository: UserRepository,
) {
    companion object {
        private const val VIDEOS_LIST_VIEW = "videos/videos"
        private const val CREATE_VIDEO_VIEW = "videos/create-video"
        private const val CREATE_VIDEO_FORM_VIEW = "partials/create-video-form"
        private const val MY_VIDEOS_VIEW = "videos/my-videos"
        private const val MY_FAVORITES_VIEW = "videos/my-favorites"
        private const val ERROR_MESSAGE = "errorMessage"
        private const val SUCCESS_MESSAGE = "successMessage"
    }

    @GetMapping("/")
    fun home() = "redirect:/videos"

    @GetMapping("/videos")
    fun showVideos(
        @RequestParam page: Int = 1,
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) searchTerm: String?,
        @RequestParam sortBy: String = "createdAt",
        @RequestParam sortDirection: String = "desc",
        model: Model,
    ): String {
        val currentUser =
            securityService.getLoginUserId()?.let { userId ->
                userRepository.findById(userId).orElse(null)
            }

        val videos = videoService.getVideos(page, categoryId, searchTerm, sortBy, sortDirection, currentUser)
        val categories = categoryService.getAllCategories()

        model.apply {
            this["videos"] = videos
            this["categories"] = categories
            this["selectedCategoryId"] = categoryId
            this["searchTerm"] = searchTerm ?: ""
            this["sortBy"] = sortBy
            this["sortDirection"] = sortDirection
        }

        return VIDEOS_LIST_VIEW
    }

    @GetMapping("/videos/new")
    fun showCreateVideoForm(model: Model): String {
        model.apply {
            this["categories"] = categoryService.getAllCategories()
            this["videoForm"] = CreateVideoForm()
        }
        return CREATE_VIDEO_VIEW
    }

    @GetMapping("/videos/youtube/search")
    @HxRequest
    fun populateCreateVideoForm(
        model: Model,
        @RequestParam videoId: String,
    ): String {
        val form =
            youTubeApi.getVideoDetails(videoId)?.let { it ->
                CreateVideoForm(
                    title = it.title,
                    url = it.url,
                    description = it.description,
                )
            } ?: CreateVideoForm()
        model.apply {
            this["categories"] = categoryService.getAllCategories()
            this["videoForm"] = form
        }
        return CREATE_VIDEO_FORM_VIEW
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
            return CREATE_VIDEO_VIEW
        }

        return try {
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
                SUCCESS_MESSAGE,
                "Your video has been submitted and is pending review",
            )
            "redirect:/videos"
        } catch (e: Exception) {
            handleError(model, e, "An error occurred while submitting your tutorial.")
            model["categories"] = categoryService.getAllCategories()
            CREATE_VIDEO_VIEW
        }
    }

    @GetMapping("/videos/my-videos")
    fun showMyVideos(model: Model): String {
        try {
            val userId = securityService.getLoginUserIdOrThrow()
            model["videos"] = videoService.getVideosByUser(userId)
        } catch (e: Exception) {
            handleError(model, e, "An error occurred while retrieving your videos.")
        }
        return MY_VIDEOS_VIEW
    }

    @GetMapping("/videos/my-favorites")
    fun showMyFavorites(model: Model): String {
        try {
            val userId = securityService.getLoginUserIdOrThrow()
            val favoritesByUser = videoService.getFavoritesByUser(userId)
            model["videos"] = mapOf("data" to favoritesByUser)
        } catch (e: Exception) {
            handleError(model, e, "An error occurred while retrieving your favorite videos.")
        }
        return MY_FAVORITES_VIEW
    }

    @PostMapping("/videos/{id}/favorite")
    @HxRequest
    fun favoriteVideo(
        @PathVariable id: Long,
    ): View = toggleFavorite(id, true)

    @PostMapping("/videos/{id}/unfavorite")
    @HxRequest
    fun unfavoriteVideo(
        @PathVariable id: Long,
    ): View = toggleFavorite(id, false)

    private fun toggleFavorite(
        id: Long,
        favorite: Boolean,
    ): View {
        val userId = securityService.getLoginUserIdOrThrow()

        if (favorite) {
            videoService.favoriteVideo(id, userId)
        } else {
            videoService.unfavoriteVideo(id, userId)
        }

        val video = videoService.getVideoById(id).orElseThrow { IllegalStateException("Video not found") }
        val templateName = if (favorite) "partials/unfavourite-video" else "partials/favourite-video"

        return FragmentsRendering.with(templateName, mapOf("video" to video)).build()
    }

    private fun handleError(
        model: Model,
        e: Exception,
        defaultMessage: String,
    ) {
        model[ERROR_MESSAGE] = e.message ?: defaultMessage
    }
}

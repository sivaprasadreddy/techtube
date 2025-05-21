package com.sivalabs.techtube.posts.web.controllers

import com.sivalabs.techtube.posts.domain.CategoryService
import com.sivalabs.techtube.posts.domain.CreatePostCmd
import com.sivalabs.techtube.posts.domain.PostService
import com.sivalabs.techtube.users.domain.SecurityService
import com.sivalabs.techtube.users.domain.UserRepository
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
class PostController(
    private val postService: PostService,
    private val categoryService: CategoryService,
    private val securityService: SecurityService,
    private val userRepository: UserRepository,
) {
    @GetMapping("/")
    fun home() = "redirect:/posts"

    @GetMapping("/posts")
    fun showPosts(
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
        val posts = postService.getPosts(page, categoryId, searchTerm, sortBy, sortDirection, currentUser)
        val categories = categoryService.getAllCategories()

        model["posts"] = posts
        model["categories"] = categories
        model["selectedCategoryId"] = categoryId
        model["searchTerm"] = searchTerm ?: ""
        model["sortBy"] = sortBy
        model["sortDirection"] = sortDirection

        return "posts/posts"
    }

    @GetMapping("/posts/new")
    fun showCreatePostForm(model: Model): String {
        val categories = categoryService.getAllCategories()
        model["categories"] = categories
        model["postForm"] = CreatePostForm()
        return "posts/create-post"
    }

    @PostMapping("/posts")
    fun createPost(
        @Valid @ModelAttribute("postForm") form: CreatePostForm,
        bindingResult: BindingResult,
        model: Model,
        redirectAttributes: RedirectAttributes,
    ): String {
        if (bindingResult.hasErrors()) {
            model["categories"] = categoryService.getAllCategories()
            return "posts/create-post"
        }

        try {
            val userId = securityService.getLoginUserIdOrThrow()

            val cmd =
                CreatePostCmd(
                    title = form.title,
                    url = form.url,
                    description = form.description,
                    categoryId = form.categoryId!!,
                    userId = userId,
                )
            postService.createPost(cmd)
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Your video has been submitted and is pending review",
            )
            return "redirect:/posts"
        } catch (e: Exception) {
            model["errorMessage"] = e.message ?: "An error occurred while submitting your tutorial."
            model["categories"] = categoryService.getAllCategories()
            return "posts/create-post"
        }
    }

    class CreatePostForm(
        @field:NotBlank(message = "Title is required")
        var title: String = "",
        @field:NotBlank(message = "URL is required")
        var url: String = "",
        @field:NotBlank(message = "Description is required")
        var description: String = "",
        @field:NotNull(message = "Category is required")
        var categoryId: Long? = null,
    )

    @GetMapping("/posts/my-posts")
    fun showMyPosts(model: Model): String {
        try {
            val userId = securityService.getLoginUserIdOrThrow()
            val posts = postService.getPostsByUser(userId)
            model["posts"] = posts
        } catch (e: Exception) {
            model["errorMessage"] = e.message ?: "An error occurred while retrieving your posts."
        }
        return "posts/my-posts"
    }

    @GetMapping("/admin/review-posts")
    fun showPostsToReview(model: Model): String {
        model["publishedPosts"] = postService.getPublishedPosts()
        model["pendingPosts"] = postService.getPendingPosts()
        model["rejectedPosts"] = postService.getRejectedPosts()
        return "admin/review-posts"
    }

    @PostMapping("/admin/posts/{id}/approve")
    fun approvePost(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ): String {
        try {
            postService.approvePost(id)
            redirectAttributes.addFlashAttribute("successMessage", "Post has been approved successfully.")
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                e.message ?: "An error occurred while approving the post.",
            )
        }
        return "redirect:/admin/review-posts"
    }

    @PostMapping("/admin/posts/{id}/reject")
    fun rejectPost(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ): String {
        try {
            postService.rejectPost(id)
            redirectAttributes.addFlashAttribute("successMessage", "Post has been rejected successfully.")
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                e.message ?: "An error occurred while rejecting the post.",
            )
        }
        return "redirect:/admin/review-posts"
    }

    @PostMapping("/admin/posts/{id}/unpublish")
    fun unpublishPost(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ): String {
        try {
            postService.unpublishPost(id)
            redirectAttributes.addFlashAttribute("successMessage", "Post has been unpublished successfully.")
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                e.message ?: "An error occurred while unpublishing the post.",
            )
        }
        return "redirect:/admin/review-posts"
    }

    @PostMapping("/admin/posts/{id}/delete")
    fun deletePost(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ): String {
        try {
            postService.deletePost(id)
            redirectAttributes.addFlashAttribute("successMessage", "Post has been deleted successfully.")
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                e.message ?: "An error occurred while deleting the post.",
            )
        }
        return "redirect:/admin/review-posts"
    }

    @PostMapping("/posts/{id}/favorite")
    @HxRequest
    fun favoritePost(
        @PathVariable id: Long,
    ): View {
        val userId = securityService.getLoginUserIdOrThrow()
        postService.favoritePost(id, userId)
        val post = postService.getPostById(id).orElseThrow { IllegalStateException("Post not found") }
        return FragmentsRendering.with("partials/unfavourite-post", mapOf("post" to post)).build()
    }

    @PostMapping("/posts/{id}/unfavorite")
    @HxRequest
    fun unfavoritePost(
        @PathVariable id: Long,
    ): View {
        val userId = securityService.getLoginUserIdOrThrow()
        postService.unfavoritePost(id, userId)
        val post = postService.getPostById(id).orElseThrow { IllegalStateException("Post not found") }
        return FragmentsRendering.with("partials/favourite-post", mapOf("post" to post)).build()
    }
}

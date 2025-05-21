package com.sivalabs.techtube.posts.web.controllers

import com.sivalabs.techtube.posts.domain.CategoryService
import com.sivalabs.techtube.posts.domain.PostService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class PostController(
    private val postService: PostService,
    private val categoryService: CategoryService,
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
        val posts = postService.getPosts(page, categoryId, searchTerm, sortBy, sortDirection)
        val categories = categoryService.getAllCategories()

        model["posts"] = posts
        model["categories"] = categories
        model["selectedCategoryId"] = categoryId
        model["searchTerm"] = searchTerm ?: ""
        model["sortBy"] = sortBy
        model["sortDirection"] = sortDirection

        return "posts"
    }
}

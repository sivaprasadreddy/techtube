package com.sivalabs.techtube.posts.web.controllers

import com.sivalabs.techtube.posts.domain.PostService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class PostController(
    private val postService: PostService,
) {
    @GetMapping("/")
    fun home() = "redirect:/posts"

    @GetMapping("/posts")
    fun showPosts(
        @RequestParam(defaultValue = "1") page: Int,
        model: Model,
    ): String {
        val posts = postService.getPosts(page)
        model["posts"] = posts
        return "posts"
    }
}

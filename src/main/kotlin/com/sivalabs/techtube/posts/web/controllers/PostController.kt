package com.sivalabs.techtube.posts.web.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class PostController {
    @GetMapping("/")
    fun home() = "redirect:/posts"

    @GetMapping("/posts")
    fun showPosts(): String = "posts"
}

package com.sivalabs.techtube.posts.domain

data class CreatePostCmd(
    val title: String,
    val url: String,
    val description: String,
    val categoryId: Long,
    val userId: Long,
)

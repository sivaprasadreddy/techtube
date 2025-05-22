package com.sivalabs.techtube.videos.domain

data class CreateVideoCmd(
    val title: String,
    val url: String,
    val description: String,
    val categoryId: Long,
    val userId: Long,
)

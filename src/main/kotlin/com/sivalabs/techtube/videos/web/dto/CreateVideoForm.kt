package com.sivalabs.techtube.videos.web.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

/**
 * Form object for creating a new video
 */
data class CreateVideoForm(
    @field:NotBlank(message = "Title is required")
    var title: String = "",
    @field:NotBlank(message = "URL is required")
    var url: String = "",
    @field:NotBlank(message = "Description is required")
    var description: String = "",
    @field:NotNull(message = "Category is required")
    var categoryId: Long? = null,
)

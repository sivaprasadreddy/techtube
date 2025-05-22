package com.sivalabs.techtube.videos.web.controllers

import com.sivalabs.techtube.BaseIT
import com.sivalabs.techtube.videos.domain.CreateVideoCmd
import com.sivalabs.techtube.videos.domain.VideoService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.jdbc.Sql

@Sql("classpath:/test-data-setup.sql")
class VideoControllerTests : BaseIT() {
    @Autowired
    lateinit var videoService: VideoService

    @Test
    fun shouldRedirectHomeToVideos() {
        val result = mockMvcTester.get().uri("/").exchange()
        assertThat(result)
            .hasStatus(HttpStatus.FOUND)
            .hasRedirectedUrl("/videos")
    }

    @Test
    fun shouldShowLatestVideos() {
        val result = mockMvcTester.get().uri("/videos").exchange()
        assertThat(result)
            .hasStatusOk()
    }

    @Test
    @WithUserDetails(NORMAL_USER_EMAIL)
    fun shouldShowCreateVideoForm() {
        val result = mockMvcTester.get().uri("/videos/new").exchange()
        assertThat(result)
            .hasStatusOk()
            .hasViewName("videos/create-video")
    }

    @Test
    @WithUserDetails(NORMAL_USER_EMAIL)
    fun shouldCreateVideoSuccessfully() {
        val result =
            mockMvcTester
                .post()
                .uri("/videos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "How Netflix Uses Java - 2025 Edition")
                .param("url", "https://www.youtube.com/watch?v=XpunFFS-n8I")
                .param("description", "How Netflix Uses Java - 2025 Edition")
                .param("categoryId", "1")
                .exchange()
        assertThat(result)
            .hasStatus(HttpStatus.FOUND)
            .hasRedirectedUrl("/videos")
            .flash()
            .containsKey("successMessage")
            .hasEntrySatisfying(
                "successMessage",
            ) { value -> assertThat(value).isEqualTo("Your video has been submitted and is pending review") }
    }

    @Test
    @WithUserDetails(NORMAL_USER_EMAIL)
    fun shouldShowMyVideos() {
        val result = mockMvcTester.get().uri("/videos/my-videos").exchange()
        assertThat(result)
            .hasStatusOk()
            .hasViewName("videos/my-videos")
    }

    @Test
    @WithUserDetails(NORMAL_USER_EMAIL)
    fun shouldFavoriteVideo() {
        val videoId = 1L
        val result =
            mockMvcTester
                .post()
                .uri("/videos/$videoId/favorite")
                .with(csrf())
                .header("HX-Request", "true")
                .exchange()

        assertThat(result).hasStatusOk()
    }

    @Test
    @WithUserDetails(NORMAL_USER_EMAIL)
    fun shouldUnfavoriteVideo() {
        // First favorite the video
        val videoId = 1L
        mockMvcTester
            .post()
            .uri("/videos/$videoId/favorite")
            .with(csrf())
            .header("HX-Request", "true")
            .exchange()

        // Then unfavorite it
        val result =
            mockMvcTester
                .post()
                .uri("/videos/$videoId/unfavorite")
                .with(csrf())
                .header("HX-Request", "true")
                .exchange()

        assertThat(result).hasStatusOk()
    }

    @Test
    @WithUserDetails(NORMAL_USER_EMAIL)
    fun shouldFailCreateVideoWithValidationErrors() {
        val result =
            mockMvcTester
                .post()
                .uri("/videos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "") // Empty title to trigger validation error
                .param("url", "https://www.youtube.com/watch?v=XpunFFS-n8I")
                .param("description", "Test Description")
                .param("categoryId", "1")
                .exchange()

        assertThat(result)
            .hasStatusOk()
            .hasViewName("videos/create-video")
            .model()
            .containsKey("categories")
            .containsKey("videoForm")
            .containsKey("org.springframework.validation.BindingResult.videoForm")
    }

    @Test
    @WithUserDetails(NORMAL_USER_EMAIL)
    fun shouldHandleExceptionDuringVideoCreation() {
        val result =
            mockMvcTester
                .post()
                .uri("/videos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "Test Video")
                .param("url", "https://www.youtube.com/watch?v=XpunFFS-n8I")
                .param("description", "Test Description")
                .param("categoryId", "999") // Invalid category ID to trigger exception
                .exchange()

        assertThat(result)
            .hasStatusOk()
            .hasViewName("videos/create-video")
            .model()
            .containsKey("categories")
            .containsKey("videoForm")
            .containsKey("errorMessage")
    }

    private fun createVideo(
        userId: Long = 1,
        categoryId: Long = 1,
    ): Long {
        val cmd =
            CreateVideoCmd(
                title = "Test Video",
                url = "https://www.youtube.com/watch?v=Lyj4of6FO4w",
                description = "Test Description",
                categoryId = categoryId,
                userId = userId,
            )
        return videoService.createVideo(cmd)
    }
}

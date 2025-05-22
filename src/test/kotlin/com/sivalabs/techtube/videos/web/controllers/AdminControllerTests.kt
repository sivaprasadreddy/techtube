package com.sivalabs.techtube.videos.web.controllers

import com.sivalabs.techtube.BaseIT
import com.sivalabs.techtube.videos.domain.CreateVideoCmd
import com.sivalabs.techtube.videos.domain.VideoService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.jdbc.Sql

@Sql("classpath:/test-data-setup.sql")
class AdminControllerTests : BaseIT() {
    @Autowired
    lateinit var videoService: VideoService

    @Test
    @WithUserDetails(ADMIN_USER_EMAIL)
    fun shouldShowVideosToReview() {
        val result = mockMvcTester.get().uri("/admin/review-videos").exchange()
        assertThat(result)
            .hasStatusOk()
            .hasViewName("admin/review-videos")
    }

    @Test
    @WithUserDetails(ADMIN_USER_EMAIL)
    fun shouldApproveVideo() {
        val videoId = createVideo()
        val result =
            mockMvcTester
                .post()
                .uri("/admin/videos/$videoId/approve")
                .with(csrf())
                .exchange()
        assertThat(result)
            .hasStatus(HttpStatus.FOUND)
            .hasRedirectedUrl("/admin/review-videos")
            .flash()
            .containsKey("successMessage")
            .hasEntrySatisfying(
                "successMessage",
            ) { value -> assertThat(value).isEqualTo("Video has been approved successfully.") }
    }

    @Test
    @WithUserDetails(ADMIN_USER_EMAIL)
    fun shouldRejectVideo() {
        val videoId = 12
        val result =
            mockMvcTester
                .post()
                .uri("/admin/videos/$videoId/reject")
                .with(csrf())
                .exchange()
        assertThat(result)
            .hasStatus(HttpStatus.FOUND)
            .hasRedirectedUrl("/admin/review-videos")
            .flash()
            .containsKey("successMessage")
            .hasEntrySatisfying(
                "successMessage",
            ) { value -> assertThat(value).isEqualTo("Video has been rejected successfully.") }
    }

    @Test
    @WithUserDetails(ADMIN_USER_EMAIL)
    fun shouldUnpublishVideo() {
        val videoId = 2
        val result =
            mockMvcTester
                .post()
                .uri("/admin/videos/$videoId/unpublish")
                .with(csrf())
                .exchange()
        assertThat(result)
            .hasStatus(HttpStatus.FOUND)
            .hasRedirectedUrl("/admin/review-videos")
            .flash()
            .containsKey("successMessage")
            .hasEntrySatisfying(
                "successMessage",
            ) { value -> assertThat(value).isEqualTo("Video has been unpublished successfully.") }
    }

    @Test
    @WithUserDetails(ADMIN_USER_EMAIL)
    fun shouldDeleteVideo() {
        val videoId = 3
        val result =
            mockMvcTester
                .post()
                .uri("/admin/videos/$videoId/delete")
                .with(csrf())
                .exchange()
        assertThat(result)
            .hasStatus(HttpStatus.FOUND)
            .hasRedirectedUrl("/admin/review-videos")
            .flash()
            .containsKey("successMessage")
            .hasEntrySatisfying(
                "successMessage",
            ) { value -> assertThat(value).isEqualTo("Video has been deleted successfully.") }
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

    // Error scenario tests
    @Test
    @WithUserDetails(ADMIN_USER_EMAIL)
    fun shouldHandleExceptionWhenApprovingNonExistentVideo() {
        val nonExistentVideoId = 9999L
        val result =
            mockMvcTester
                .post()
                .uri("/admin/videos/$nonExistentVideoId/approve")
                .with(csrf())
                .exchange()
        assertThat(result)
            .hasStatus(HttpStatus.FOUND)
            .hasRedirectedUrl("/admin/review-videos")
            .flash()
            .containsKey("errorMessage")
            .hasEntrySatisfying(
                "errorMessage",
            ) { value -> assertThat(value).isEqualTo("Video not found with id: 9999") }
    }

    @Test
    @WithUserDetails(ADMIN_USER_EMAIL)
    fun shouldHandleExceptionWhenRejectingNonExistentVideo() {
        val nonExistentVideoId = 9999L
        val result =
            mockMvcTester
                .post()
                .uri("/admin/videos/$nonExistentVideoId/reject")
                .with(csrf())
                .exchange()
        assertThat(result)
            .hasStatus(HttpStatus.FOUND)
            .hasRedirectedUrl("/admin/review-videos")
            .flash()
            .containsKey("errorMessage")
            .hasEntrySatisfying(
                "errorMessage",
            ) { value -> assertThat(value).isEqualTo("Video not found with id: 9999") }
    }

    @Test
    @WithUserDetails(ADMIN_USER_EMAIL)
    fun shouldHandleExceptionWhenUnpublishingNonExistentVideo() {
        val nonExistentVideoId = 9999L
        val result =
            mockMvcTester
                .post()
                .uri("/admin/videos/$nonExistentVideoId/unpublish")
                .with(csrf())
                .exchange()
        assertThat(result)
            .hasStatus(HttpStatus.FOUND)
            .hasRedirectedUrl("/admin/review-videos")
            .flash()
            .containsKey("errorMessage")
            .hasEntrySatisfying(
                "errorMessage",
            ) { value -> assertThat(value).isEqualTo("Video not found with id: 9999") }
    }

    @Test
    @WithUserDetails(ADMIN_USER_EMAIL)
    fun shouldHandleNonExistentVideoDelete() {
        val nonExistentVideoId = 9999L
        val result =
            mockMvcTester
                .post()
                .uri("/admin/videos/$nonExistentVideoId/delete")
                .with(csrf())
                .exchange()
        assertThat(result)
            .hasStatus(HttpStatus.FOUND)
            .hasRedirectedUrl("/admin/review-videos")
            .flash()
            .containsKey("errorMessage")
            .hasEntrySatisfying(
                "errorMessage",
            ) { value -> assertThat(value).isEqualTo("Video not found with id: 9999") }
    }

    // Unauthorized access tests
    @Test
    @WithUserDetails(NORMAL_USER_EMAIL)
    fun shouldDenyNormalUserAccessToReviewVideos() {
        val result = mockMvcTester.get().uri("/admin/review-videos").exchange()
        assertThat(result)
            .hasStatus(HttpStatus.FORBIDDEN)
    }

    @Test
    @WithUserDetails(NORMAL_USER_EMAIL)
    fun shouldDenyNormalUserAccessToApproveVideo() {
        val videoId = 1L
        val result =
            mockMvcTester
                .post()
                .uri("/admin/videos/$videoId/approve")
                .with(csrf())
                .exchange()
        assertThat(result)
            .hasStatus(HttpStatus.FORBIDDEN)
    }

    @Test
    @WithUserDetails(NORMAL_USER_EMAIL)
    fun shouldDenyNormalUserAccessToRejectVideo() {
        val videoId = 1L
        val result =
            mockMvcTester
                .post()
                .uri("/admin/videos/$videoId/reject")
                .with(csrf())
                .exchange()
        assertThat(result)
            .hasStatus(HttpStatus.FORBIDDEN)
    }

    @Test
    @WithUserDetails(NORMAL_USER_EMAIL)
    fun shouldDenyNormalUserAccessToUnpublishVideo() {
        val videoId = 1L
        val result =
            mockMvcTester
                .post()
                .uri("/admin/videos/$videoId/unpublish")
                .with(csrf())
                .exchange()
        assertThat(result)
            .hasStatus(HttpStatus.FORBIDDEN)
    }

    @Test
    @WithUserDetails(NORMAL_USER_EMAIL)
    fun shouldDenyNormalUserAccessToDeleteVideo() {
        val videoId = 1L
        val result =
            mockMvcTester
                .post()
                .uri("/admin/videos/$videoId/delete")
                .with(csrf())
                .exchange()
        assertThat(result)
            .hasStatus(HttpStatus.FORBIDDEN)
    }
}

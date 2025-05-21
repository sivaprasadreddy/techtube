package com.sivalabs.techtube.posts.web.controllers

import com.sivalabs.techtube.BaseIT
import com.sivalabs.techtube.posts.domain.CreatePostCmd
import com.sivalabs.techtube.posts.domain.PostService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.jdbc.Sql

@Sql("classpath:/test-data-setup.sql")
class PostControllerTests : BaseIT() {
    @Autowired
    lateinit var postService: PostService

    @Test
    fun shouldShowLatestPosts() {
        val result = mockMvcTester.get().uri("/posts").exchange()
        assertThat(result)
            .hasStatusOk()
    }

    @Test
    @WithUserDetails(NORMAL_USER_EMAIL)
    fun shouldShowCreatePostForm() {
        val result = mockMvcTester.get().uri("/posts/new").exchange()
        assertThat(result)
            .hasStatusOk()
            .hasViewName("posts/create-post")
    }

    @Test
    @WithUserDetails(NORMAL_USER_EMAIL)
    fun shouldCreatePostSuccessfully() {
        val result =
            mockMvcTester
                .post()
                .uri("/posts")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "How Netflix Uses Java - 2025 Edition")
                .param("url", "https://www.youtube.com/watch?v=XpunFFS-n8I")
                .param("description", "How Netflix Uses Java - 2025 Edition")
                .param("categoryId", "1")
                .exchange()
        assertThat(result)
            .hasStatus(HttpStatus.FOUND)
            .hasRedirectedUrl("/posts")
            .flash()
            .containsKey("successMessage")
            .hasEntrySatisfying(
                "successMessage",
            ) { value -> assertThat(value).isEqualTo("Your video has been submitted and is pending review") }
    }

    @Test
    @WithUserDetails(NORMAL_USER_EMAIL)
    fun shouldShowMyPosts() {
        val result = mockMvcTester.get().uri("/posts/my-posts").exchange()
        assertThat(result)
            .hasStatusOk()
            .hasViewName("posts/my-posts")
    }

    @Test
    @WithUserDetails(ADMIN_USER_EMAIL)
    fun shouldShowPostsToReview() {
        val result = mockMvcTester.get().uri("/admin/review-posts").exchange()
        assertThat(result)
            .hasStatusOk()
            .hasViewName("admin/review-posts")
    }

    @Test
    @WithUserDetails(ADMIN_USER_EMAIL)
    fun shouldApprovePost() {
        val postId = createPost()
        val result =
            mockMvcTester
                .post()
                .uri("/admin/posts/$postId/approve")
                .with(csrf())
                .exchange()
        assertThat(result)
            .hasStatus(HttpStatus.FOUND)
            .hasRedirectedUrl("/admin/review-posts")
            .flash()
            .containsKey("successMessage")
            .hasEntrySatisfying(
                "successMessage",
            ) { value -> assertThat(value).isEqualTo("Post has been approved successfully.") }
    }

    @Test
    @WithUserDetails(ADMIN_USER_EMAIL)
    fun shouldRejectPost() {
        val postId = 12
        val result =
            mockMvcTester
                .post()
                .uri("/admin/posts/$postId/reject")
                .with(csrf())
                .exchange()
        assertThat(result)
            .hasStatus(HttpStatus.FOUND)
            .hasRedirectedUrl("/admin/review-posts")
            .flash()
            .containsKey("successMessage")
            .hasEntrySatisfying(
                "successMessage",
            ) { value -> assertThat(value).isEqualTo("Post has been rejected successfully.") }
    }

    @Test
    @WithUserDetails(ADMIN_USER_EMAIL)
    fun shouldUnpublishPost() {
        val postId = 2
        val result =
            mockMvcTester
                .post()
                .uri("/admin/posts/$postId/unpublish")
                .with(csrf())
                .exchange()
        assertThat(result)
            .hasStatus(HttpStatus.FOUND)
            .hasRedirectedUrl("/admin/review-posts")
            .flash()
            .containsKey("successMessage")
            .hasEntrySatisfying(
                "successMessage",
            ) { value -> assertThat(value).isEqualTo("Post has been unpublished successfully.") }
    }

    @Test
    @WithUserDetails(ADMIN_USER_EMAIL)
    fun shouldDeletePost() {
        val postId = 3
        val result =
            mockMvcTester
                .post()
                .uri("/admin/posts/$postId/delete")
                .with(csrf())
                .exchange()
        assertThat(result)
            .hasStatus(HttpStatus.FOUND)
            .hasRedirectedUrl("/admin/review-posts")
            .flash()
            .containsKey("successMessage")
            .hasEntrySatisfying(
                "successMessage",
            ) { value -> assertThat(value).isEqualTo("Post has been deleted successfully.") }
    }

    private fun createPost(
        userId: Long = 1,
        categoryId: Long = 1,
    ): Long {
        val cmd =
            CreatePostCmd(
                title = "Test Post",
                url = "https://www.youtube.com/watch?v=Lyj4of6FO4w",
                description = "Test Description",
                categoryId = categoryId,
                userId = userId,
            )
        return postService.createPost(cmd)
    }
}

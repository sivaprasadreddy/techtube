package com.sivalabs.techtube.posts.web.controllers

import com.sivalabs.techtube.BaseIT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf

class PostControllerTests : BaseIT() {
    @Test
    fun shouldShowLatestPosts() {
        val result = mockMvcTester.get().uri("/posts").exchange()
        assertThat(result)
            .hasStatusOk()
    }

    @Test
    @WithUserDetails("siva@gmail.com")
    fun shouldCreateUserSuccessfully() {
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
}

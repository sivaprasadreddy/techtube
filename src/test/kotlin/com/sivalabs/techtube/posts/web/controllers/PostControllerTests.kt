package com.sivalabs.techtube.posts.web.controllers

import com.sivalabs.techtube.BaseIT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PostControllerTests : BaseIT() {
    @Test
    fun shouldShowLatestPosts() {
        val result = mockMvcTester.get().uri("/posts").exchange()
        assertThat(result)
            .hasStatusOk()
    }
}

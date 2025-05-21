package com.sivalabs.techtube.users.web.controllers

import com.sivalabs.techtube.BaseIT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf

class LoginControllerTests : BaseIT() {
    @Test
    fun shouldLoginWithValidCredentialsSuccessfully() {
        val result =
            mockMvcTester
                .post()
                .uri("/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "siva@gmail.com")
                .param("password", "siva")

        assertThat(result).hasStatus(HttpStatus.FOUND).headers().hasValue("Location", "/")
    }

    @Test
    fun shouldNotLoginWithInvalidCredentials() {
        val result =
            mockMvcTester
                .post()
                .uri("/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "dummy@gmail.com")
                .param("password", "invalid")

        assertThat(result).hasStatus(HttpStatus.FOUND).headers().hasValue("Location", "/login?error")
    }
}

package com.sivalabs.techtube.users.web.controllers

import com.sivalabs.techtube.BaseIT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors

class RegistrationControllerTests : BaseIT() {
    @Test
    fun shouldRegisterUserSuccessfully() {
        val result =
            mockMvcTester
                .post()
                .uri("/register")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "testuser123")
                .param("email", "testuser123@gmail.com")
                .param("password", "testuser123")

        assertThat(result).hasStatus(HttpStatus.FOUND).hasViewName("redirect:/login")
    }

    @Test
    fun shouldNotRegisterUserWithInvalidData() {
        val result =
            mockMvcTester
                .post()
                .uri("/register")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "")
                .param("email", "testuser123gmail.com")
                .param("password", "testuser123")

        assertThat(result).hasStatus(HttpStatus.OK).hasViewName("auth/registration")
    }
}

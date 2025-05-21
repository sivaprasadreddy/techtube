package com.sivalabs.techtube.users.web.dto

import jakarta.validation.constraints.NotBlank

class RegistrationRequest(
    @field:NotBlank(message = "Name is required") var name: String = "",
    @field:NotBlank(message = "Email is required") var email: String = "",
    @field:NotBlank(message = "Password is required") var password: String = "",
)

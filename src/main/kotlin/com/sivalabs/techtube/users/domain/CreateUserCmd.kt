package com.sivalabs.techtube.users.domain

data class CreateUserCmd(
    val name: String,
    val email: String,
    val password: String,
    val role: Role,
)

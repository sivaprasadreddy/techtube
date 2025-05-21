package com.sivalabs.techtube.users.domain

data class UserDTO(
    val id: Long,
    val name: String,
    val email: String,
    val role: Role,
)

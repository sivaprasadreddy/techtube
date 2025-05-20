package com.sivalabs.techtube.users.domain

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class SecurityService(
    private val userRepository: UserRepository,
) {
    fun getLoginUserId(): Long? {
        SecurityContextHolder.getContext().authentication?.let {
            val username = it.name
            return userRepository.findByEmail(username)?.id
        }
        return null
    }
}

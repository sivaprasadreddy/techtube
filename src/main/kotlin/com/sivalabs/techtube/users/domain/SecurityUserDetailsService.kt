package com.sivalabs.techtube.users.domain

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class SecurityUserDetailsService(
    private val userService: UserService,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user =
            userService.findByEmail(username)
                ?: throw UsernameNotFoundException("Invalid username/password.")
        return SecurityUser(
            user.id!!,
            user.name,
            user.email,
            user.password,
            user.role,
        )
    }
}

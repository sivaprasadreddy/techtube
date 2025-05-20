package com.sivalabs.techtube.users.domain

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
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
        return org.springframework.security.core.userdetails.User(
            user.email,
            user.password,
            getAuthorities(user),
        )
    }

    private fun getAuthorities(user: User): List<GrantedAuthority> = listOf(SimpleGrantedAuthority(user.role.name))
}

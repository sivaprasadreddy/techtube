package com.sivalabs.techtube.users.domain

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun findByEmail(email: String): User? = userRepository.findByEmail(email)

    fun createUser(user: RegistrationRequest): User {
        if (userRepository.findByEmail(user.email) != null) {
            throw UserAlreadyExistsException("User with email ${user.email} already exists")
        }
        val newUser = User()
        newUser.name = user.name
        newUser.email = user.email
        newUser.password = passwordEncoder.encode(user.password)
        newUser.role = Role.ROLE_USER
        return userRepository.save(newUser)
    }
}

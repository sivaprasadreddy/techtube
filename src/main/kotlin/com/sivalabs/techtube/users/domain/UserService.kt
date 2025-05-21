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

    fun createUser(cmd: CreateUserCmd): User {
        if (userRepository.findByEmail(cmd.email) != null) {
            throw UserAlreadyExistsException("User with email ${cmd.email} already exists")
        }
        val newUser = User()
        newUser.name = cmd.name
        newUser.email = cmd.email
        newUser.password = passwordEncoder.encode(cmd.password)
        newUser.role = cmd.role
        return userRepository.save(newUser)
    }
}

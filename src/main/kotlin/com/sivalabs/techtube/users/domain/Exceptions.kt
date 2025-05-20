package com.sivalabs.techtube.users.domain

class UserAlreadyExistsException(
    message: String,
) : RuntimeException(message)

class UserNotFoundException(
    message: String,
) : RuntimeException(message)

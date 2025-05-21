package com.sivalabs.techtube.users.domain

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class SecurityService {
    fun getLoginUserIdOrThrow(): Long = getLoginUserId() ?: throw IllegalStateException("User not authenticated")

    fun getLoginUserId(): Long? {
        SecurityContextHolder.getContext().authentication?.let {
            if (it.principal is SecurityUser) {
                return (it.principal as SecurityUser).getId()
            }
            return null
        }
        return null
    }
}

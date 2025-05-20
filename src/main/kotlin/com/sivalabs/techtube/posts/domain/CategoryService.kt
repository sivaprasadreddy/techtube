package com.sivalabs.techtube.posts.domain

import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
)

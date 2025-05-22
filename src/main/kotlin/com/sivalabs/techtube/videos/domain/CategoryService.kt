package com.sivalabs.techtube.videos.domain

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
) {
    @Transactional(readOnly = true)
    fun getAllCategories(): List<Category> = categoryRepository.findAll()
}

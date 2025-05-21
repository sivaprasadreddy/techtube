package com.sivalabs.techtube.posts.domain

import com.sivalabs.techtube.common.models.PagedResult
import com.sivalabs.techtube.users.domain.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostService(
    private val postRepository: PostRepository,
    private val categoryRepository: CategoryRepository,
    private val userService: UserService,
) {
    @Transactional(readOnly = true)
    fun getPosts(
        page: Int,
        categoryId: Long? = null,
        searchTerm: String? = null,
        sortBy: String = "createdAt",
        sortDirection: String = "desc",
    ): PagedResult<Post> {
        val direction = if (sortDirection.equals("asc", ignoreCase = true)) Sort.Direction.ASC else Sort.Direction.DESC
        val pageable = PageRequest.of(page - 1, 6, Sort.by(direction, sortBy))

        val postsPage =
            if (categoryId != null || !searchTerm.isNullOrBlank()) {
                postRepository.findPostsByCategoryAndSearchTerm(categoryId, searchTerm, pageable)
            } else {
                postRepository.findAllPosts(pageable)
            }

        return PagedResult.of(postsPage)
    }
}

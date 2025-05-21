package com.sivalabs.techtube.posts.domain

import com.sivalabs.techtube.common.models.PagedResult
import com.sivalabs.techtube.users.domain.SecurityService
import com.sivalabs.techtube.users.domain.UserRepository
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
    private val userRepository: UserRepository,
    private val securityService: SecurityService,
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

    @Transactional
    fun createPost(cmd: CreatePostCmd) {
        val user = userRepository.findById(cmd.userId).orElseThrow { IllegalStateException("User not found") }
        val category = categoryRepository.findById(cmd.categoryId).orElseThrow { IllegalArgumentException("Category not found") }

        val post = Post()
        post.title = cmd.title
        post.url = cmd.url
        post.description = cmd.description
        post.category = category
        post.createdBy = user
        post.status = PostStatus.PENDING

        postRepository.save(post)
    }

    @Transactional(readOnly = true)
    fun getPendingPosts(): List<Post> {
        return postRepository.findAllPendingPosts()
    }
}

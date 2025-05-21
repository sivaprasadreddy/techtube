package com.sivalabs.techtube.posts.domain

import com.sivalabs.techtube.common.models.PagedResult
import com.sivalabs.techtube.users.domain.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.collections.map

@Service
class PostService(
    private val postRepository: PostRepository,
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository,
    private val postMapper: PostMapper,
) {
    @Transactional(readOnly = true)
    fun getPosts(
        page: Int,
        categoryId: Long? = null,
        searchTerm: String? = null,
        sortBy: String = "createdAt",
        sortDirection: String = "desc",
    ): PagedResult<PostDTO> {
        val direction = if (sortDirection.equals("asc", ignoreCase = true)) Sort.Direction.ASC else Sort.Direction.DESC
        val pageable = PageRequest.of(page - 1, 6, Sort.by(direction, sortBy))

        val postsPage =
            if (categoryId != null || !searchTerm.isNullOrBlank()) {
                postRepository.findPostsByCategoryAndSearchTerm(categoryId, searchTerm, pageable).map { postMapper.toDTO(it) }
            } else {
                postRepository.findAllPosts(pageable).map { postMapper.toDTO(it) }
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
    fun getPublishedPosts(): List<PostDTO> = postRepository.findAllPostsByStatus(PostStatus.APPROVED).map { postMapper.toDTO(it) }

    @Transactional(readOnly = true)
    fun getPendingPosts(): List<PostDTO> = postRepository.findAllPostsByStatus(PostStatus.PENDING).map { postMapper.toDTO(it) }

    @Transactional(readOnly = true)
    fun getRejectedPosts(): List<PostDTO> = postRepository.findAllPostsByStatus(PostStatus.REJECTED).map { postMapper.toDTO(it) }

    @Transactional
    fun approvePost(postId: Long) {
        val post =
            postRepository
                .findById(postId)
                .orElseThrow { IllegalArgumentException("Post not found with id: $postId") }
        post.status = PostStatus.APPROVED
        postRepository.save(post)
    }

    @Transactional
    fun rejectPost(postId: Long) {
        val post =
            postRepository
                .findById(postId)
                .orElseThrow { IllegalArgumentException("Post not found with id: $postId") }
        post.status = PostStatus.REJECTED
        postRepository.save(post)
    }

    @Transactional
    fun unpublishPost(postId: Long) {
        val post =
            postRepository
                .findById(postId)
                .orElseThrow { IllegalArgumentException("Post not found with id: $postId") }
        post.status = PostStatus.PENDING
        postRepository.save(post)
    }

    @Transactional
    fun deletePost(postId: Long) {
        postRepository.deleteById(postId)
    }

    @Transactional(readOnly = true)
    fun getPostsByUser(userId: Long): List<Post> = postRepository.findPostsByUserId(userId)
}

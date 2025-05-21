package com.sivalabs.techtube.posts.domain

import com.sivalabs.techtube.BaseIT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@Transactional
class PostServiceTests : BaseIT() {
    @Autowired
    private lateinit var postService: PostService

    @Autowired
    private lateinit var postRepository: PostRepository

    @Test
    fun shouldApprovePost() {
        // Given a pending post
        val post = createPendingPost()
        val postId = post.id!!

        // When approving the post
        postService.approvePost(postId)

        // Then the post status should be APPROVED
        val approvedPost = postRepository.findById(postId).orElseThrow()
        assertThat(approvedPost.status).isEqualTo(PostStatus.APPROVED)
    }

    @Test
    fun shouldRejectPost() {
        // Given a pending post
        val post = createPendingPost()
        val postId = post.id!!

        // When rejecting the post
        postService.rejectPost(postId)

        // Then the post status should be REJECTED
        val rejectedPost = postRepository.findById(postId).orElseThrow()
        assertThat(rejectedPost.status).isEqualTo(PostStatus.REJECTED)
    }

    private fun createPendingPost(): Post {
        // Create a new post with PENDING status
        val post = Post()
        post.title = "Test Post"
        post.url = "https://example.com/test"
        post.description = "Test Description"
        post.status = PostStatus.PENDING

        // We need to set category and createdBy, but these depend on the database state
        // For simplicity, we'll use the first category and user found in the database
        val categories = postRepository.findAll().map { it.category }.distinctBy { it?.id }
        val users = postRepository.findAll().map { it.createdBy }.distinctBy { it?.id }

        post.category = categories.firstOrNull()
        post.createdBy = users.firstOrNull()

        return postRepository.save(post)
    }
}

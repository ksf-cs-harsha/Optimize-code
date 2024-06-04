package com.harsha.organize.service

import com.harsha.organize.entity.Comment
import com.harsha.organize.entity.Post
import com.harsha.organize.excepetion.ResourceNotFoundException
import com.harsha.organize.repo.CommentRepository
import com.harsha.organize.repo.PostRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class PostService(
    val postRepository: PostRepository,
    private val commentRepository: CommentRepository
) {
    suspend fun getPostById(postId: Int): Post? {
        return postRepository.findById(postId.toLong())
    }

    suspend fun getPosts(page: Int, size: Int): List<Post> {
        return postRepository.findAllBy(PageRequest.of(page - 1, size))
    }

    suspend fun getCommentsByPostId(postId: Int): List<Comment> {
        val comments = commentRepository.findAllCommentsByPostId(postId)
        if (comments.isEmpty()) {
            throw ResourceNotFoundException("No comments found for post with id $postId")
        }
        return comments
    }
}

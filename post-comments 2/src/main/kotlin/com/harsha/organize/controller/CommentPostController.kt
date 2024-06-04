package com.harsha.organize.controller

import com.harsha.organize.excepetion.ResourceNotFoundException
import com.harsha.organize.output.CommentOutput
import com.harsha.organize.output.PostOutput
import com.harsha.organize.service.PostService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/api/v2/"], produces = [MediaType.APPLICATION_JSON_VALUE])
class CommentPostController(private val postService: PostService) {

    @GetMapping("post/{postId}")
    suspend fun getPostWithComments(@PathVariable postId: Int): PostOutput {
        val optionalPost = postService.getPostById(postId)
            ?: throw ResourceNotFoundException("Post not found for the given id $postId")
        val commentsMap = postService.getCommentsByPostId(postId)
        val commentOutputs = commentsMap.map { comment ->
            postId
            CommentOutput(comment.id, comment.name, comment.email, comment.body)
        }
        return PostOutput(optionalPost.id, optionalPost.userId, optionalPost.name, commentOutputs)
    }

    @GetMapping("/posts")
    suspend fun getPosts(@RequestParam(defaultValue = "1") page: Int): PostsResponseDTO {
        val pageSize = 10
        val posts = postService.getPosts(page, pageSize)

        if (posts.isEmpty()) {
            throw ResourceNotFoundException("No posts found for page $page")
        }

        val postOutputs = posts.map { post ->
            val commentOutputs = postService.getCommentsByPostId(post.id).map { comment ->
                CommentOutput(comment.id, comment.name, comment.email, comment.body)
            }
            if (commentOutputs.isEmpty()) {
                throw ResourceNotFoundException("No comments found for post ${post.id}")
            }

            PostOutput(post.id, post.userId, post.name, commentOutputs)
        }

        val total = postService.postRepository.count()
        return PostsResponseDTO(postOutputs, page, total)
    }

    data class PostsResponseDTO(val feeds: List<PostOutput>, val page: Int, val total: Long)
}

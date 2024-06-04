package com.harsha.organize.service

import com.harsha.organize.dto.CommentData
import com.harsha.organize.dto.PostData
import com.harsha.organize.entity.Comment
import com.harsha.organize.entity.Post
import com.harsha.organize.repo.CommentRepository
import com.harsha.organize.repo.PostRepository
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.toEntity
import java.time.LocalDateTime

@Service
class DataLoadingService(
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val webClient: WebClient
) {

    private val logger = LoggerFactory.getLogger(DataLoadingService::class.java)
    private val baseURL = "https://jsonplaceholder.typicode.com/"

    suspend fun loadPostsAndComments() {

        logger.info("In loadPostsAndComments")

        val postDataList = webClient.get()
            .uri("$baseURL/posts")
            .retrieve()
            .toEntity<List<PostData>>()
            .awaitSingle()
            .body

        if (postDataList != null) {
            logger.info("Downloaded posts = {}", postDataList.size)
            for (postData in postDataList) {
                val post = Post(
                    id = postData.id,
                    userId = postData.userId,
                    name = postData.title,
                    body = postData.body,
                    createAt = LocalDateTime.now()
                )

                postRepository.save(post)
                val commentDataList = webClient.get()
                    .uri("${baseURL}/comments?postId=${postData.id}")
                    .retrieve()
                    .toEntity<List<CommentData>>()
                    .awaitSingle()
                    .body

                if (commentDataList != null) {
                    logger.info("Downloaded comments for post {} = {}", postData.id, commentDataList.size)
                    for (commentData in commentDataList) {
                        val comment = Comment(
                            id = commentData.id,
                            name = commentData.name,
                            email = commentData.email,
                            body = commentData.body,
                            createAt = LocalDateTime.now()
                        )

                        commentRepository.save(comment)
                    }

                }
            }
        }
        logger.info("Return from loadPostsAndComments")
    }


}


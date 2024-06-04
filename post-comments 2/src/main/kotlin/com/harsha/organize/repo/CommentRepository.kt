package com.harsha.organize.repo

import com.harsha.organize.entity.Comment
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : CoroutineCrudRepository<Comment, Int> {

    @Query("select * from comment where post_id = :postId")
    suspend fun findAllCommentsByPostId(postId: Int): List<Comment>
}

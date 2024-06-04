package com.harsha.organize.repo

import com.harsha.organize.entity.Post
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : CoroutineCrudRepository<Post, Long> {

    suspend fun findAllBy(pageRequest: PageRequest): List<Post>
}

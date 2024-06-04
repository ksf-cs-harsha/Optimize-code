package com.harsha.organize.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "post")
data class Post(
    @Id
    var id: Int,
    @Column(value = "user_id")
    var userId: Int,
    var name: String = "",
    var body: String = "",
    var createAt: LocalDateTime,
    var updateAt: LocalDateTime = LocalDateTime.now()
)
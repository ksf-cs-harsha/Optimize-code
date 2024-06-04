package com.harsha.organize.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime


@Table(name = "comment")
data class Comment(
    @Id
    var id: Int,
    var name: String = "",
    var email: String = "",
    var body: String = "",
    var createAt: LocalDateTime,
    var updateAt: LocalDateTime = LocalDateTime.now()
)


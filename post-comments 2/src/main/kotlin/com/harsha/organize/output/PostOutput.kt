package com.harsha.organize.output

data class PostOutput(
    val id: Int, val userId: Int, val title: String, val commentDataList: List<CommentOutput>
)

package com.project.tex.post.model

data class PostListData(
    val hasError: Boolean = false,
    val code: Int? = null,
    val listData: List<Any>? = null,
): java.io.Serializable
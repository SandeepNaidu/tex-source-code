package com.project.tex.main.model


import com.google.gson.annotations.SerializedName

data class AvidDetailForArtist(
    @SerializedName("artistId")
    val artistId: Int? = null,
    @SerializedName("avidId")
    val avidId: Int? = null,
    @SerializedName("caption")
    val caption: String? = null,
    @SerializedName("content")
    val content: String? = null,
    @SerializedName("coverContent")
    val coverContent: String? = null,
    @SerializedName("createAt")
    val createAt: String? = null,
    @SerializedName("hashTag")
    val hashTag: String? = null,
    @SerializedName("isLiked")
    var isLiked: Int? = null,
    @SerializedName("isSaved")
    var isSaved: Int? = null,
    @SerializedName("isShared")
    var isShared: Int? = null,
    @SerializedName("likeCount")
    var likeCount: Int? = null,
    @SerializedName("mode")
    val mode: String? = null,
    @SerializedName("saveCount")
    var saveCount: Int? = null,
    @SerializedName("shareCount")
    var shareCount: Int? = null,
    @SerializedName("tags")
    val tags: List<Tag?>? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("updateAt")
    val updateAt: String? = null
) : java.io.Serializable
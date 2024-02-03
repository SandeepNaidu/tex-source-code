package com.project.tex.main.model


import com.google.gson.annotations.SerializedName
import com.project.tex.post.model.AllPostData

data class AvidData(
    @SerializedName("artistFirstName")
    var artistFirstName: String?,
    @SerializedName("artistLastName")
    var artistLastName: String?,
    @SerializedName("artistId")
    val artistId: Int?,
    @SerializedName("avidId")
    val avidId: Int?,
    @SerializedName("caption")
    val caption: String?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("coverContent")
    val coverContent: String?,
    @SerializedName("createAt")
    val createAt: String?,
    @SerializedName("hashTag")
    val hashTag: String?,
    @SerializedName("mode")
    val mode: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("updateAt")
    val updateAt: String?
) : java.io.Serializable, AllPostData.Body.Posts.DateComparable(createAt, 0)
package com.project.tex.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "local_post", indices = [Index(value = ["postLocalUri"], unique = true)])
data class LocalPost(
    @PrimaryKey()
    val postId: String,
    @ColumnInfo(name = "postLocalUri")
    val postLocalUri: String?,
    @ColumnInfo(name = "postThumb")
    val postThumb: String?,
    @ColumnInfo(name = "postType")
    val postType: String,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP", name = "createdAt")
    val createdAt: String = Date().toString(),
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP", name = "updatedAt")
    val updatedAt: String? = Date().toString()
)
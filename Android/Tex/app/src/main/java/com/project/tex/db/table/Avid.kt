package com.project.tex.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "avid")
data class Avid(
    @PrimaryKey()
    val avidId: String,
    @ColumnInfo(name = "avidLocalUri")
    val avidLocalUri: String?,
    @ColumnInfo(name = "title")
    val title: String?,
    @ColumnInfo(name = "caption")
    val caption: String?,
    @ColumnInfo(name = "tags")
    val tags: String?,
    @ColumnInfo(name = "avidMode")
    val avidMode: String,
    @ColumnInfo(name = "avidCoverFrame")
    val avidCoverFrame: Int,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP", name = "createdAt")
    val createdAt: String = Date().toString(),
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP", name = "updatedAt")
    val updatedAt: String? = Date().toString()
)
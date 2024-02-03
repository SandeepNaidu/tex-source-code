package com.project.tex.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "avid_takes")
data class AvidTakes(
    @PrimaryKey(autoGenerate = true)
    val avidTakeId: Int = 0,
    @ColumnInfo(name = "avidId")
    val avidId: String,
    @ColumnInfo(name = "videoUrl")
    val videoUrl: String?,
    @ColumnInfo(name = "videoThumb")
    val videoThumb: String?,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP", name = "createdAt")
    val createdAt: String = Date().toString(),
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP", name = "updatedAt")
    val updatedAt: String = Date().toString()
)
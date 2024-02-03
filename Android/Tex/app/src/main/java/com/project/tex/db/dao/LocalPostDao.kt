package com.project.tex.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.project.tex.db.table.LocalPost
import io.reactivex.Single

@Dao
interface LocalPostDao {
    @Query("SELECT * FROM local_post order by createdAt desc limit 3")
    fun getAll(): LiveData<List<LocalPost>>

    @Query("SELECT * FROM local_post WHERE postId IN (:postIds)")
    fun loadAllByIds(postIds: IntArray): Single<List<LocalPost>>

    @Query("SELECT * FROM local_post WHERE postLocalUri IN (:mPostLocalUri)")
    fun loadAllLocalUri(mPostLocalUri: String): Single<List<LocalPost>>

    @Query("SELECT * FROM local_post WHERE postType =:type")
    fun loadPostByType(type: String): LiveData<List<LocalPost>>

    @Query("SELECT * FROM local_post WHERE postId=:mPostId")
    fun loadAllByAvidId(mPostId: Int): Single<List<LocalPost>>

    @Insert
    fun insertAll(vararg avid: LocalPost): Single<List<Long>>

    @Query("DELETE from local_post where postId =:mPostId")
    fun deletePostById(mPostId: String): Single<Int>

    @Delete
    fun delete(post: LocalPost): Single<Int>
}
package com.project.tex.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.project.tex.db.table.Avid
import io.reactivex.Single

@Dao
interface AvidDao {
    @Query("SELECT * FROM avid")
    fun getAll(): List<Avid>

    @Query("SELECT * FROM avid WHERE avidId IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Avid>

//    @Query("SELECT * FROM avid WHERE first_name LIKE :first AND " +
//           "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): Avid

    @Insert
    fun insertAll(vararg users: Avid) : Single<List<Long>>

    @Delete
    fun delete(user: Avid): Single<Int>
}
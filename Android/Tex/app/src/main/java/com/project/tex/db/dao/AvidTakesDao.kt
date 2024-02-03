package com.project.tex.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.project.tex.db.table.Avid
import com.project.tex.db.table.AvidTakes
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface AvidTakesDao {
    @Query("SELECT * FROM avid_takes order by createdAt desc limit 3")
    fun getAll(): LiveData<List<AvidTakes>>

    @Query("SELECT * FROM avid_takes WHERE avidTakeId IN (:avidIds)")
    fun loadAllByIds(avidIds: IntArray): Single<List<AvidTakes>>

    @Query("SELECT * FROM avid_takes WHERE avidId=:avidId")
    fun loadAllByAvidId(avidId: Int): Single<List<AvidTakes>>

//    @Query("SELECT * FROM avid WHERE first_name LIKE :first AND " +
//           "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): Avid

    //DELETE FROM `table`
    //WHERE id NOT IN (
    //  SELECT id
    //  FROM (
    //    SELECT id
    //    FROM `table`
    //    ORDER BY id DESC
    //    LIMIT 42 -- keep this many records
    //  ) foo
    //);
    @Query("DELETE from avid_takes where avidTakeId NOT IN ( SELECT avidTakeId FROM (SELECT avidTakeId FROM avid_takes ORDER BY avidTakeId DESC LIMIT :keepAvid))")
    fun deleteAllOlderAvidTake(keepAvid: Int = 3): Completable

    @Query("SELECT * from avid_takes where avidTakeId NOT IN ( SELECT avidTakeId FROM (SELECT avidTakeId FROM avid_takes ORDER BY avidTakeId DESC LIMIT :keepAvid))")
    fun getAllOlderAvidTakes(keepAvid: Int = 3): Observable<List<AvidTakes>>

    @Insert
    fun insertAll(vararg avid: AvidTakes): Single<List<Long>>

    @Query("DELETE from avid_takes where avidTakeId =:avidId")
    fun deleteAvidTakeById(avidId: Int): Single<Int>

    @Query("DELETE from avid_takes where avidId =:avidId")
    fun deleteAvidTakeByAvidId(avidId: String): Single<Int>

    @Delete
    fun delete(avid: AvidTakes): Single<Int>
}
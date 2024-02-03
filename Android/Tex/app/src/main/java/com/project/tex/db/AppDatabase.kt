package com.project.tex.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.project.tex.db.dao.AvidDao
import com.project.tex.db.dao.AvidTakesDao
import com.project.tex.db.dao.LocalPostDao
import com.project.tex.db.table.Avid
import com.project.tex.db.table.AvidTakes
import com.project.tex.db.table.LocalPost

@Database(entities = [Avid::class, AvidTakes::class, LocalPost::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun avidDao(): AvidDao
    abstract fun avidTakesDao(): AvidTakesDao
    abstract fun localPostDao(): LocalPostDao
}
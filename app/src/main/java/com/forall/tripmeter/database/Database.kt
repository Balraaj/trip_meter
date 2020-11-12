package com.forall.tripmeter.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.forall.tripmeter.database.dao.TripDao
import com.forall.tripmeter.database.entity.Trip

@Database(entities = [Trip::class], version = 2)
abstract class Database: RoomDatabase() {
    abstract fun tripDao(): TripDao
}
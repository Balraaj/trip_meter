package com.forall.tripmeter.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.forall.tripmeter.database.dao.TripDao
import com.forall.tripmeter.database.dao.TripLocationDao
import com.forall.tripmeter.database.entity.Trip
import com.forall.tripmeter.database.entity.TripLocation

@Database(entities = [Trip::class, TripLocation::class], version = 1)
abstract class Database: RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun locationDao(): TripLocationDao
}
package com.kacesontisdel.matchtracking.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

// shortcut: exportSchema off and no migrations, since this is a personal sideloaded app; add both before changing the schema with data you care about.
@Database(entities = [SetEntity::class, GameEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun setDao(): SetDao
}

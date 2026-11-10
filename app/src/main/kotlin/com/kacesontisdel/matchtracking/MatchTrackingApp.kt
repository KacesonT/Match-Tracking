package com.kacesontisdel.matchtracking

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.kacesontisdel.matchtracking.data.SetRepository
import com.kacesontisdel.matchtracking.data.db.AppDatabase

/** Manual DI: one place that builds the long-lived objects. `by lazy` = created on first use, then reused. */
class AppContainer(context: Context) {
  private val database by lazy { Room.databaseBuilder(context, AppDatabase::class.java, "match-tracking.db").build() }
  val setRepository by lazy { SetRepository(database.setDao()) }
}

class MatchTrackingApp : Application() {
  val container by lazy { AppContainer(this) }
}

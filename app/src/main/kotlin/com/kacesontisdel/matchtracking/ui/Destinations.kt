package com.kacesontisdel.matchtracking.ui

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// Each screen in the app is identified by a "key". A key is just a tiny object.
// `data object` = a class with exactly one instance (a singleton).
// @Serializable lets Navigation 3 save the back stack when Android recreates the activity.
@Serializable data object LogSetKey : NavKey

@Serializable data object StatsKey : NavKey

@Serializable data object StageBansKey : NavKey

@Serializable data object BackupKey : NavKey

// A drawer entry pairs a screen key with the text shown in the menu and top bar.
data class Destination(val key: NavKey, val label: String)

// The order here is the order of the items in the drawer.
val destinations =
  listOf(
    Destination(LogSetKey, "Log Set"),
    Destination(StatsKey, "Stats"),
    Destination(StageBansKey, "Stage Bans"),
    Destination(BackupKey, "Backup"),
  )

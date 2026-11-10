package com.kacesontisdel.matchtracking.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Table "sets". format is the SetFormat name ("Bo3"/"Bo5") so no type converter is needed.
@Entity(tableName = "sets")
data class SetEntity(
  @PrimaryKey val id: String,
  val dateMillis: Long,
  val format: String,
  val opponentName: String?,
  val iWon: Boolean,
)

// Table "games". Deleting a set deletes its games (CASCADE). characters/stages are the string ids from Roster.kt.
@Entity(
  tableName = "games",
  foreignKeys = [
    ForeignKey(
      entity = SetEntity::class,
      parentColumns = ["id"],
      childColumns = ["setId"],
      onDelete = ForeignKey.CASCADE,
    ),
  ],
  indices = [Index("setId")], // SQLite needs an index on the FK column for fast cascades
)
data class GameEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val setId: String,
  val gameNumber: Int,
  val stageId: String,
  val myCharacterId: String,
  val opponentCharacterId: String,
  val won: Boolean,
)

package com.kacesontisdel.matchtracking.data

import com.kacesontisdel.matchtracking.domain.GameSet
import com.kacesontisdel.matchtracking.domain.SetFormat
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// The backup file format. @Serializable makes the compiler generate the JSON reading/writing code.
// `version` lets a future app change the format and still recognise old files.
@Serializable
data class BackupFile(val version: Int = 1, val sets: List<BackupSet>)

@Serializable
data class BackupSet(
  val id: String,
  val dateMillis: Long,
  val format: String,
  val opponentName: String?,
  val games: List<BackupGame>,
)

@Serializable
data class BackupGame(
  val number: Int,
  val stageId: String,
  val myCharacterId: String,
  val opponentCharacterId: String,
  val won: Boolean,
)

data class ImportResult(val added: Int, val skipped: Int)

val backupJson = Json {
  prettyPrint = true
  ignoreUnknownKeys = true // tolerate files written by a newer app version
}

/**
 * Rebuilds the set through the same rules as live logging, so a bad file is rejected instead of
 * saved: unknown format/stage/character, a set that never finished, or extra games after the end.
 * "Did I win" is recomputed from the games, so the file can't contradict itself.
 */
internal fun BackupSet.toGameSet(): GameSet {
  require(id.isNotBlank()) { "A set has no id" }
  var set = GameSet(id = id, dateMillis = dateMillis, format = SetFormat.valueOf(format), opponentName = opponentName)
  for (g in games.sortedBy { it.number }) {
    require(STAGES.any { it.id == g.stageId }) { "Unknown stage '${g.stageId}'" }
    require(FIGHTERS.any { it.id == g.myCharacterId }) { "Unknown character '${g.myCharacterId}'" }
    require(FIGHTERS.any { it.id == g.opponentCharacterId }) { "Unknown character '${g.opponentCharacterId}'" }
    set = set.addGame(g.stageId, g.myCharacterId, g.opponentCharacterId, g.won)
  }
  require(set.isComplete) { "Set $id is not finished" }
  return set
}

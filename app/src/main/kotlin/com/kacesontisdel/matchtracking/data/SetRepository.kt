package com.kacesontisdel.matchtracking.data

import com.kacesontisdel.matchtracking.data.db.GameEntity
import com.kacesontisdel.matchtracking.data.db.SetDao
import com.kacesontisdel.matchtracking.data.db.SetEntity
import com.kacesontisdel.matchtracking.domain.GameSet
import com.kacesontisdel.matchtracking.domain.SetFormat
import kotlin.random.Random

class SetRepository(private val dao: SetDao) {
  /** Incomplete sets are never saved. */
  suspend fun saveSet(set: GameSet) {
    require(set.isComplete) { "Cannot save an incomplete set" }
    dao.insertSetWithGames(
      SetEntity(set.id, set.dateMillis, set.format.name, set.opponentName, set.iWon),
      set.games.map {
        GameEntity(
          setId = set.id,
          gameNumber = it.number,
          stageId = it.stageId,
          myCharacterId = it.myCharacterId,
          opponentCharacterId = it.opponentCharacterId,
          won = it.won,
        )
      },
    )
  }

  /** Debug helper: saves [count] random completed sets, using only a few characters so stats have repeats. */
  suspend fun insertSampleSets(count: Int = 25) {
    val mine = FIGHTERS.take(4).map { it.id }
    val theirs = FIGHTERS.take(10).map { it.id }
    repeat(count) {
      var set = GameSet(
        dateMillis = System.currentTimeMillis() - it * 86_400_000L,
        format = SetFormat.entries.random(),
      )
      while (!set.isComplete) {
        set = set.addGame(STAGES.random().id, mine.random(), theirs.random(), won = Random.nextBoolean())
      }
      saveSet(set)
    }
  }

  fun stats(byStage: Boolean, byOpponent: Boolean, myCharacterId: String?, opponentCharacterId: String?) =
    dao.stats(byStage, byOpponent, myCharacterId, opponentCharacterId)

  /** Every saved set with its games, as pretty-printed JSON. */
  suspend fun exportJson(): String {
    val gamesBySet = dao.allGames().groupBy { it.setId }
    val sets = dao.allSets().map { s ->
      BackupSet(
        id = s.id,
        dateMillis = s.dateMillis,
        format = s.format,
        opponentName = s.opponentName,
        games = gamesBySet[s.id].orEmpty().map {
          BackupGame(it.gameNumber, it.stageId, it.myCharacterId, it.opponentCharacterId, it.won)
        },
      )
    }
    return backupJson.encodeToString(BackupFile(sets = sets))
  }

  /**
   * Adds sets whose id isn't in the database yet. Everything is validated before anything is saved,
   * so a bad file throws and changes nothing.
   */
  suspend fun importJson(text: String): ImportResult {
    val file = backupJson.decodeFromString<BackupFile>(text)
    require(file.version == 1) { "Unsupported backup version ${file.version}" }
    val known = dao.existingSetIds().toMutableSet()
    var skipped = 0
    val toAdd = file.sets.mapNotNull { s ->
      // add() returns false if the id was already there (in the database or earlier in this file).
      if (known.add(s.id)) {
        s.toGameSet()
      } else {
        skipped++
        null
      }
    }
    toAdd.forEach { saveSet(it) }
    return ImportResult(added = toAdd.size, skipped = skipped)
  }

  suspend fun setCount(): Int = dao.setCount()
  suspend fun gameCount(): Int = dao.gameCount()
}

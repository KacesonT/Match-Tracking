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

  suspend fun setCount(): Int = dao.setCount()
  suspend fun gameCount(): Int = dao.gameCount()
}

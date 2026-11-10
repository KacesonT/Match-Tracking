package com.kacesontisdel.matchtracking.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SetDao {
  @Insert
  protected abstract suspend fun insertSet(set: SetEntity)

  @Insert
  protected abstract suspend fun insertGames(games: List<GameEntity>)

  /** Set + games are written together or not at all. */
  @Transaction
  open suspend fun insertSetWithGames(set: SetEntity, games: List<GameEntity>) {
    insertSet(set)
    insertGames(games)
  }

  @Query("SELECT COUNT(*) FROM sets")
  abstract suspend fun setCount(): Int

  @Query("SELECT COUNT(*) FROM games")
  abstract suspend fun gameCount(): Int

  /**
   * One query for all three stats tabs. A switched-off dimension comes back as '' so every row
   * collapses into one group. [myCharacterId] / [opponentCharacterId] = null means all.
   */
  @Query(
    """
    SELECT CASE WHEN :byStage THEN g.stageId ELSE '' END AS stageId,
           CASE WHEN :byOpponent THEN g.opponentCharacterId ELSE '' END AS opponentCharacterId,
           COUNT(*) AS gamesPlayed,
           SUM(g.won) AS gamesWon,
           COUNT(DISTINCT s.id) AS setsPlayed,
           COUNT(DISTINCT CASE WHEN s.iWon THEN s.id END) AS setsWon
    FROM games g JOIN sets s ON s.id = g.setId
    WHERE (:myCharacterId IS NULL OR g.myCharacterId = :myCharacterId)
      AND (:opponentCharacterId IS NULL OR g.opponentCharacterId = :opponentCharacterId)
    GROUP BY 1, 2
    ORDER BY gamesPlayed DESC
    """
  )
  abstract fun stats(
    byStage: Boolean,
    byOpponent: Boolean,
    myCharacterId: String?,
    opponentCharacterId: String?,
  ): Flow<List<StatRow>>
}

/** One result row. Empty stageId / opponentCharacterId means that dimension wasn't grouped on. */
data class StatRow(
  val stageId: String,
  val opponentCharacterId: String,
  val gamesPlayed: Int,
  val gamesWon: Int,
  val setsPlayed: Int,
  val setsWon: Int,
)

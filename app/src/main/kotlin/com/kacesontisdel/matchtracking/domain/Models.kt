package com.kacesontisdel.matchtracking.domain

import java.util.UUID

/** Bo3 ends at 2 wins, Bo5 at 3. */
enum class SetFormat(val winsNeeded: Int) { Bo3(2), Bo5(3) }

data class Game(
  val number: Int,
  val stageId: String,
  val myCharacterId: String,
  val opponentCharacterId: String,
  val won: Boolean,
)

/**
 * A set in progress or finished. Immutable: every change returns a new GameSet.
 * "Did I win the set" is derived from the games, never stored separately.
 */
data class GameSet(
  val id: String = UUID.randomUUID().toString(),
  val dateMillis: Long,
  val format: SetFormat,
  val opponentName: String? = null, // never used in stats
  val games: List<Game> = emptyList(),
) {
  val myWins: Int get() = games.count { it.won }
  val opponentWins: Int get() = games.count { !it.won }
  val isComplete: Boolean get() = myWins >= format.winsNeeded || opponentWins >= format.winsNeeded

  /** Only meaningful once [isComplete]. */
  val iWon: Boolean get() = myWins >= format.winsNeeded

  /** Appends the next game (numbered automatically). Rejected once the set is over. */
  fun addGame(stageId: String, myCharacterId: String, opponentCharacterId: String, won: Boolean): GameSet {
    check(!isComplete) { "Set is already complete" }
    return copy(games = games + Game(games.size + 1, stageId, myCharacterId, opponentCharacterId, won))
  }

  /** Removes the most recent game; does nothing on an empty set. Reopens a finished set. */
  fun undoLastGame(): GameSet = copy(games = games.dropLast(1))
}

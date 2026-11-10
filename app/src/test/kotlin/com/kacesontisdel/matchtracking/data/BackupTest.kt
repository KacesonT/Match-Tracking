package com.kacesontisdel.matchtracking.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class BackupTest {
  private val stage = STAGES.first().id
  private val me = FIGHTERS[0].id
  private val them = FIGHTERS[1].id

  private fun game(n: Int, won: Boolean) = BackupGame(n, stage, me, them, won)

  private fun set(format: String = "Bo3", games: List<BackupGame>) =
    BackupSet(id = "abc", dateMillis = 1L, format = format, opponentName = null, games = games)

  private fun assertRejected(s: BackupSet) {
    try {
      s.toGameSet()
      fail("expected the set to be rejected")
    } catch (_: IllegalArgumentException) {
    } catch (_: IllegalStateException) {
    }
  }

  @Test
  fun validSetIsRebuiltAndWinIsRecomputed() {
    val result = set(games = listOf(game(2, true), game(1, true))).toGameSet() // out of order on purpose
    assertTrue(result.isComplete)
    assertTrue(result.iWon)
    assertEquals(listOf(1, 2), result.games.map { it.number })
  }

  @Test
  fun unfinishedSetIsRejected() = assertRejected(set(games = listOf(game(1, true))))

  @Test
  fun unknownStageIsRejected() =
    assertRejected(set(games = listOf(game(1, true).copy(stageId = "nope"), game(2, true))))

  @Test
  fun jsonRoundTrips() {
    val file = BackupFile(sets = listOf(set(games = listOf(game(1, true), game(2, true)))))
    assertEquals(file, backupJson.decodeFromString<BackupFile>(backupJson.encodeToString(file)))
  }
}

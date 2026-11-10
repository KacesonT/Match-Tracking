package com.kacesontisdel.matchtracking.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class GameSetTest {
  private fun newSet(format: SetFormat) = GameSet(dateMillis = 0L, format = format)

  private fun GameSet.play(vararg results: Boolean): GameSet =
    results.fold(this) { set, won -> set.addGame("battlefield", "mario", "link", won) }

  @Test
  fun bo3EndsAtTwoWins() {
    val set = newSet(SetFormat.Bo3).play(true)
    assertFalse(set.isComplete)
    assertTrue(set.play(true).isComplete)
  }

  @Test
  fun bo3EndsAtTwoLossesToo() {
    val set = newSet(SetFormat.Bo3).play(false, false)
    assertTrue(set.isComplete)
    assertFalse(set.iWon)
  }

  @Test
  fun bo3WinInThreeGames() {
    val set = newSet(SetFormat.Bo3).play(true, false, true)
    assertTrue(set.isComplete)
    assertTrue(set.iWon)
    assertEquals(2, set.myWins)
    assertEquals(1, set.opponentWins)
  }

  @Test
  fun bo5NotCompleteAtTwoWins() {
    assertFalse(newSet(SetFormat.Bo5).play(true, true).isComplete)
  }

  @Test
  fun bo5EndsAtThreeWins() {
    val set = newSet(SetFormat.Bo5).play(true, false, true, false, true)
    assertTrue(set.isComplete)
    assertTrue(set.iWon)
  }

  @Test
  fun cannotAddGameToCompleteSet() {
    val set = newSet(SetFormat.Bo3).play(true, true)
    assertThrows(IllegalStateException::class.java) { set.play(true) }
  }

  @Test
  fun undoReopensFinishedSet() {
    val set = newSet(SetFormat.Bo3).play(true, true).undoLastGame()
    assertFalse(set.isComplete)
    assertEquals(1, set.games.size)
  }

  @Test
  fun undoOnEmptySetDoesNothing() {
    assertEquals(0, newSet(SetFormat.Bo3).undoLastGame().games.size)
  }

  @Test
  fun gameNumbersCountUpFromOne() {
    val set = newSet(SetFormat.Bo5).play(true, false, true)
    assertEquals(listOf(1, 2, 3), set.games.map { it.number })
  }
}

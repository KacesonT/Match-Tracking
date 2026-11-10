package com.kacesontisdel.matchtracking.data

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Test

/** Every roster id must have a matching res/drawable/<prefix>_<id>.png, or GameIcon silently shows text. */
class RosterIconsTest {
  // Gradle runs unit tests with the module folder (app/) as the working directory.
  private val drawables = File("src/main/res/drawable")

  private fun missing(prefix: String, ids: List<String>) =
    ids.filter { !File(drawables, "${prefix}_$it.png").exists() }

  @Test
  fun everyStageHasAnIcon() = assertEquals(emptyList<String>(), missing("stg", STAGES.map { it.id }))

  @Test
  fun everyFighterHasAnIcon() = assertEquals(emptyList<String>(), missing("chr", FIGHTERS.map { it.id }))
}

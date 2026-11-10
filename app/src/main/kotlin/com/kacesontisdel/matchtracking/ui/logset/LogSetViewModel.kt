package com.kacesontisdel.matchtracking.ui.logset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kacesontisdel.matchtracking.MatchTrackingApp
import com.kacesontisdel.matchtracking.data.SetRepository
import com.kacesontisdel.matchtracking.domain.GameSet
import com.kacesontisdel.matchtracking.domain.SetFormat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LogSetUiState(
  // Setup screen
  val format: SetFormat = SetFormat.Bo3,
  val opponentName: String = "",
  val justSaved: Boolean = false,
  // The set in progress; null means we are on the setup screen.
  val set: GameSet? = null,
  // Picks for the NEXT game
  val stageId: String? = null,
  val myCharacterId: String? = null,
  val opponentCharacterId: String? = null,
  // shortcut: recents live in memory only (reset on app restart); persist or derive from the DB later.
  val recentMine: List<String> = emptyList(),
  val recentOpponent: List<String> = emptyList(),
  val saving: Boolean = false,
  val saveError: String? = null,
) {
  val canRecordGame: Boolean
    get() = set != null && !set.isComplete && stageId != null && myCharacterId != null && opponentCharacterId != null
}

class LogSetViewModel(private val repository: SetRepository) : ViewModel() {
  private val _state = MutableStateFlow(LogSetUiState())
  val state: StateFlow<LogSetUiState> = _state.asStateFlow()

  fun setFormat(format: SetFormat) = _state.update { it.copy(format = format) }

  fun setOpponentName(name: String) = _state.update { it.copy(opponentName = name) }

  fun startSet() = _state.update {
    it.copy(
      set = GameSet(
        dateMillis = System.currentTimeMillis(),
        format = it.format,
        opponentName = it.opponentName.trim().ifEmpty { null },
      ),
      stageId = null,
      opponentCharacterId = null,
      justSaved = false,
      saveError = null,
    )
  }

  fun pickStage(id: String) = _state.update { it.copy(stageId = id) }

  fun pickMine(id: String) = _state.update { it.copy(myCharacterId = id, recentMine = it.recentMine.withRecent(id)) }

  fun pickOpponent(id: String) =
    _state.update { it.copy(opponentCharacterId = id, recentOpponent = it.recentOpponent.withRecent(id)) }

  /** Adds the game. Stage and both characters stay selected, so the next game starts with the same picks. */
  fun recordGame(won: Boolean) = _state.update { s ->
    val set = s.set
    if (!s.canRecordGame || set == null) {
      s
    } else {
      s.copy(set = set.addGame(s.stageId!!, s.myCharacterId!!, s.opponentCharacterId!!, won), saveError = null)
    }
  }

  fun undoLastGame() = _state.update { it.copy(set = it.set?.undoLastGame(), saveError = null) }

  /** Abandons the set in progress without saving. */
  fun discardSet() = _state.update { it.copy(set = null, stageId = null, opponentCharacterId = null, saveError = null) }

  fun saveSet() {
    val s = _state.value
    val set = s.set ?: return
    if (!set.isComplete || s.saving) return // second tap while saving would hit a duplicate id
    _state.update { it.copy(saving = true, saveError = null) }
    viewModelScope.launch {
      try {
        repository.saveSet(set)
        _state.update { it.copy(set = null, saving = false, justSaved = true, opponentName = "") }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        // Keep the finished set on screen so nothing is lost; the user can retry.
        _state.update { it.copy(saving = false, saveError = "Couldn't save: ${e.message}") }
      }
    }
  }

  private fun List<String>.withRecent(id: String): List<String> = (listOf(id) + (this - id)).take(MAX_RECENTS)

  companion object {
    private const val MAX_RECENTS = 8

    // Manual DI: pull the repository out of the Application's container.
    val Factory = viewModelFactory {
      initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MatchTrackingApp
        LogSetViewModel(app.container.setRepository)
      }
    }
  }
}

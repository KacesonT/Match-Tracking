package com.kacesontisdel.matchtracking.ui.logset

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kacesontisdel.matchtracking.data.fighterName
import com.kacesontisdel.matchtracking.data.stageName
import com.kacesontisdel.matchtracking.domain.GameSet
import com.kacesontisdel.matchtracking.domain.SetFormat
import com.kacesontisdel.matchtracking.ui.components.CharacterPicker
import com.kacesontisdel.matchtracking.ui.components.STAGE_GRID_HEIGHT
import com.kacesontisdel.matchtracking.ui.components.StagePicker

private enum class Sheet { Mine, Opponent }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogSetScreen(viewModel: LogSetViewModel = viewModel(factory = LogSetViewModel.Factory)) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  var sheet by remember { mutableStateOf<Sheet?>(null) }

  Column(
    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    val set = state.set
    if (set == null) {
      SetupSection(state, viewModel)
    } else {
      ScoreHeader(set)
      if (set.isComplete) {
        SummarySection(set, state.saving, state.saveError, onSave = viewModel::saveSet)
      } else {
        NextGameSection(
          state,
          onOpen = { sheet = it },
          onStageTap = viewModel::tapStage,
          onToggleStageMode = viewModel::toggleStageMode,
          onResult = viewModel::recordGame,
        )
      }
      GameList(set)
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(onClick = viewModel::undoLastGame, enabled = set.games.isNotEmpty() && !state.saving) {
          Text("Undo last game")
        }
        TextButton(onClick = viewModel::discardSet, enabled = !state.saving) { Text("Discard set") }
      }
    }
  }

  sheet?.let { which ->
    ModalBottomSheet(onDismissRequest = { sheet = null }) {
      when (which) {
        Sheet.Mine -> CharacterPicker(
          recentIds = state.recentMine,
          onPick = { viewModel.pickMine(it.id); sheet = null },
        )
        Sheet.Opponent -> CharacterPicker(
          recentIds = state.recentOpponent,
          onPick = { viewModel.pickOpponent(it.id); sheet = null },
        )
      }
    }
  }
}

@Composable
private fun SetupSection(state: LogSetUiState, vm: LogSetViewModel) {
  Text("New set", style = MaterialTheme.typography.titleLarge)
  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    SetFormat.entries.forEach { format ->
      FilterChip(
        selected = state.format == format,
        onClick = { vm.setFormat(format) },
        label = { Text(format.name) },
      )
    }
  }
  OutlinedTextField(
    value = state.opponentName,
    onValueChange = vm::setOpponentName,
    label = { Text("Opponent name (optional)") },
    singleLine = true,
    modifier = Modifier.fillMaxWidth(),
  )
  Button(onClick = vm::startSet, modifier = Modifier.fillMaxWidth()) { Text("Start set") }
  if (state.justSaved) Text("Set saved ✓", color = MaterialTheme.colorScheme.primary)
}

@Composable
private fun ScoreHeader(set: GameSet) {
  Text(
    "${set.format.name} · You ${set.myWins} – ${set.opponentWins} ${set.opponentName ?: "Opponent"}",
    style = MaterialTheme.typography.titleLarge,
  )
}

@Composable
private fun NextGameSection(
  state: LogSetUiState,
  onOpen: (Sheet) -> Unit,
  onStageTap: (String) -> Unit,
  onToggleStageMode: () -> Unit,
  onResult: (Boolean) -> Unit,
) {
  Text("Game ${(state.set?.games?.size ?: 0) + 1}", style = MaterialTheme.typography.titleMedium)
  PickButton("My character", state.myCharacterId?.let(::fighterName)) { onOpen(Sheet.Mine) }
  PickButton("Opponent's character", state.opponentCharacterId?.let(::fighterName)) { onOpen(Sheet.Opponent) }

  // Same ban / select flow as the Stage Bans screen; the chosen stage is what gets recorded with the game.
  Text(
    "Stage: ${state.stageId?.let(::stageName) ?: "not chosen"}" +
      if (state.selectingStage) " · tap a stage to play on it" else " · tap a stage to ban it",
  )
  StagePicker(
    banned = state.bannedStageIds,
    selected = state.stageId,
    onPick = { onStageTap(it.id) },
    modifier = Modifier.height(STAGE_GRID_HEIGHT),
    horizontalPadding = 0.dp, // the screen already pads its content
  )
  OutlinedButton(onClick = onToggleStageMode, modifier = Modifier.fillMaxWidth()) {
    Text(if (state.selectingStage) "Mode: Select stage (tap to switch to Ban)" else "Mode: Ban stages (tap to switch to Select)")
  }
  Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
    Button(onClick = { onResult(true) }, enabled = state.canRecordGame, modifier = Modifier.weight(1f)) {
      Text("I won")
    }
    Button(onClick = { onResult(false) }, enabled = state.canRecordGame, modifier = Modifier.weight(1f)) {
      Text("I lost")
    }
  }
}

@Composable
private fun PickButton(label: String, value: String?, onClick: () -> Unit) {
  OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
    Text(if (value == null) "$label: tap to choose" else "$label: $value")
  }
}

@Composable
private fun SummarySection(set: GameSet, saving: Boolean, error: String?, onSave: () -> Unit) {
  Text(
    if (set.iWon) "You won the set ${set.myWins}–${set.opponentWins}" else "You lost the set ${set.myWins}–${set.opponentWins}",
    style = MaterialTheme.typography.titleMedium,
  )
  Button(onClick = onSave, enabled = !saving, modifier = Modifier.fillMaxWidth()) {
    Text(if (saving) "Saving…" else "Save set")
  }
  if (error != null) Text(error, color = MaterialTheme.colorScheme.error)
}

@Composable
private fun GameList(set: GameSet) {
  set.games.forEach { g ->
    Text(
      "G${g.number} · ${stageName(g.stageId)} · ${fighterName(g.myCharacterId)} vs ${fighterName(g.opponentCharacterId)} · ${if (g.won) "W" else "L"}",
      style = MaterialTheme.typography.bodyMedium,
    )
  }
}

package com.kacesontisdel.matchtracking.ui.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kacesontisdel.matchtracking.data.db.StatRow
import com.kacesontisdel.matchtracking.data.fighterName
import com.kacesontisdel.matchtracking.data.stageName
import com.kacesontisdel.matchtracking.ui.components.CharacterPicker
import com.kacesontisdel.matchtracking.ui.components.GameIcon
import com.kacesontisdel.matchtracking.ui.components.STAGE_ASPECT
import kotlin.math.roundToInt

/** Rows with fewer games than this are greyed out: too small a sample to trust. */
private const val MIN_GAMES = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: StatsViewModel = viewModel(factory = StatsViewModel.Factory)) {
  val tab by viewModel.tab.collectAsStateWithLifecycle()
  val mine by viewModel.myCharacterId.collectAsStateWithLifecycle()
  val opponent by viewModel.opponentCharacterId.collectAsStateWithLifecycle()
  val rows by viewModel.rows.collectAsStateWithLifecycle()
  // Which filter the character sheet is currently choosing for; null = sheet closed.
  var picking by remember { mutableStateOf<Filter?>(null) }

  Column(modifier = Modifier.fillMaxSize()) {
    PrimaryTabRow(selectedTabIndex = tab.ordinal) {
      StatsTab.entries.forEach {
        Tab(selected = it == tab, onClick = { viewModel.selectTab(it) }, text = { Text(it.label, maxLines = 1) })
      }
    }
    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
      FilterButton("Playing as", mine, onClick = { picking = Filter.Mine }, onClear = { viewModel.filterByCharacter(null) }, modifier = Modifier.weight(1f))
      FilterButton("Versus", opponent, onClick = { picking = Filter.Opponent }, onClear = { viewModel.filterByOpponent(null) }, modifier = Modifier.weight(1f))
    }

    LazyColumn(
      modifier = Modifier.weight(1f),
      contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 4.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      if (rows.isEmpty()) item { Text("No games match.") }
      items(rows, key = { "${it.stageId}|${it.opponentCharacterId}" }) { StatRowItem(it, tab) }
    }
  }

  picking?.let { which ->
    ModalBottomSheet(onDismissRequest = { picking = null }) {
      CharacterPicker(
        recentIds = emptyList(),
        onPick = {
          if (which == Filter.Mine) viewModel.filterByCharacter(it.id) else viewModel.filterByOpponent(it.id)
          picking = null
        },
      )
    }
  }
}

private enum class Filter { Mine, Opponent }

/** Shows "label: character" (or All); a small × appears when a character is chosen and clears it. */
@Composable
private fun FilterButton(label: String, characterId: String?, onClick: () -> Unit, onClear: () -> Unit, modifier: Modifier) {
  Column(modifier = modifier) {
    OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
      Text("$label: ${characterId?.let(::fighterName) ?: "All"}", maxLines = 1)
    }
    if (characterId != null) TextButton(onClick = onClear) { Text("Clear") }
  }
}

@Composable
private fun StatRowItem(row: StatRow, tab: StatsTab) {
  val label = when (tab) {
    StatsTab.VsCharacter -> "vs ${fighterName(row.opponentCharacterId)}"
    StatsTab.ByStage -> stageName(row.stageId)
    StatsTab.StageVsCharacter -> "${stageName(row.stageId)} · vs ${fighterName(row.opponentCharacterId)}"
  }
  val winPct = (row.gamesWon * 100f / row.gamesPlayed).roundToInt()
  Row(
    modifier = Modifier.fillMaxWidth().alpha(if (row.gamesPlayed < MIN_GAMES) 0.5f else 1f),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    // The tab says which ids are filled in (the other is ""), so only those pictures are shown.
    if (tab.byStage) {
      GameIcon(
        prefix = "stg",
        id = row.stageId,
        name = stageName(row.stageId),
        modifier = Modifier.width(72.dp).aspectRatio(STAGE_ASPECT).clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop,
      )
    }
    if (tab.byOpponent) {
      GameIcon(prefix = "chr", id = row.opponentCharacterId, name = fighterName(row.opponentCharacterId), modifier = Modifier.size(48.dp))
    }
    Column {
      Text(label, style = MaterialTheme.typography.titleMedium)
      Text("Games ${row.gamesWon}–${row.gamesPlayed - row.gamesWon} ($winPct%)")
      Text(
        "Sets ${row.setsWon}–${row.setsPlayed - row.setsWon}",
        style = MaterialTheme.typography.bodySmall,
      )
    }
  }
}

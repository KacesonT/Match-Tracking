package com.kacesontisdel.matchtracking.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kacesontisdel.matchtracking.data.FIGHTERS
import com.kacesontisdel.matchtracking.data.Fighter

/**
 * Searchable icon grid. [recentIds] (most recent first) are pinned at the top while the search box is empty.
 * The picker doesn't remember anything itself; the caller owns the recents and updates them in [onPick].
 */
@Composable
fun CharacterPicker(recentIds: List<String>, onPick: (Fighter) -> Unit, modifier: Modifier = Modifier) {
  var query by remember { mutableStateOf("") }
  val matches = remember(query) { FIGHTERS.filter { it.name.contains(query.trim(), ignoreCase = true) } }
  val recents = remember(recentIds) { recentIds.mapNotNull { id -> FIGHTERS.firstOrNull { it.id == id } } }

  Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
    OutlinedTextField(
      value = query,
      onValueChange = { query = it },
      label = { Text("Search characters") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
    )
    LazyVerticalGrid(
      columns = GridCells.Adaptive(minSize = 76.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.padding(top = 12.dp),
    ) {
      if (query.isBlank() && recents.isNotEmpty()) {
        item(span = { GridItemSpan(maxLineSpan) }) { SectionLabel("Recent") }
        items(recents, key = { "recent_${it.id}" }) { FighterCell(it, onPick) }
        item(span = { GridItemSpan(maxLineSpan) }) { SectionLabel("All characters") }
      }
      items(matches, key = { it.id }) { FighterCell(it, onPick) }
    }
  }
}

@Composable
private fun SectionLabel(text: String) {
  Text(text, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 4.dp))
}

@Composable
private fun FighterCell(fighter: Fighter, onPick: (Fighter) -> Unit) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.clickable { onPick(fighter) },
  ) {
    // GameIcon already shows the name as text when the icon file is missing.
    GameIcon(prefix = "chr", id = fighter.id, name = fighter.name, modifier = Modifier.size(64.dp))
  }
}

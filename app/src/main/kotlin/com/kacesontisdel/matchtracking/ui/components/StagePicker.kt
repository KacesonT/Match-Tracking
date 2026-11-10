package com.kacesontisdel.matchtracking.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kacesontisdel.matchtracking.data.STAGES
import com.kacesontisdel.matchtracking.data.Stage

/** Two-column grid of the 9 stages; GameIcon shows the name as text until stg_<id>.png exists. */
@Composable
fun StagePicker(onPick: (Stage) -> Unit, modifier: Modifier = Modifier) {
  LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier.padding(horizontal = 16.dp),
  ) {
    items(STAGES, key = { it.id }) { stage ->
      GameIcon(
        prefix = "stg",
        id = stage.id,
        name = stage.name,
        modifier = Modifier.fillMaxWidth().height(84.dp).clickable { onPick(stage) },
      )
    }
  }
}

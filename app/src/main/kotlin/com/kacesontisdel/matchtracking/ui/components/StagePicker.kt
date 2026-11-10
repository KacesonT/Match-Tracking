package com.kacesontisdel.matchtracking.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kacesontisdel.matchtracking.data.STAGES
import com.kacesontisdel.matchtracking.data.Stage

private val CELL_HEIGHT = 84.dp
private val GAP = 8.dp

/**
 * Height that fits every stage with no inner scrolling. A lazy grid needs a bounded height, so screens
 * that scroll as a whole pass this in as the modifier.
 */
val STAGE_GRID_HEIGHT: Dp = ((STAGES.size + 1) / 2).let { rows -> CELL_HEIGHT * rows + GAP * (rows - 1) }

/**
 * Two-column grid of the stages in STAGES; GameIcon shows the name as text until stg_<id>.png exists.
 * Banned stages get a red X on top, the [selected] stage gets a green check.
 */
@Composable
fun StagePicker(
  onPick: (Stage) -> Unit,
  modifier: Modifier = Modifier,
  banned: Set<String> = emptySet(),
  selected: String? = null,
  horizontalPadding: Dp = 16.dp,
) {
  LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    verticalArrangement = Arrangement.spacedBy(GAP),
    horizontalArrangement = Arrangement.spacedBy(GAP),
    modifier = modifier.padding(horizontal = horizontalPadding),
  ) {
    items(STAGES, key = { it.id }) { stage ->
      // Box stacks its children on top of each other: the icon first, then the overlay symbol.
      Box(
        modifier = Modifier.fillMaxWidth().height(CELL_HEIGHT).clickable { onPick(stage) },
        contentAlignment = Alignment.Center,
      ) {
        GameIcon(prefix = "stg", id = stage.id, name = stage.name, modifier = Modifier.matchParentSize())
        if (stage.id in banned) {
          Icon(Icons.Default.Close, contentDescription = "Banned", tint = Color.Red, modifier = Modifier.size(56.dp))
        } else if (stage.id == selected) {
          Icon(
            Icons.Default.Check,
            contentDescription = "Selected",
            tint = Color(0xFF2E7D32),
            modifier = Modifier.size(56.dp),
          )
        }
      }
    }
  }
}

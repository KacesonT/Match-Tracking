package com.kacesontisdel.matchtracking.ui.stagebans

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kacesontisdel.matchtracking.ui.components.StageModeButton
import com.kacesontisdel.matchtracking.ui.components.StagePicker

/** Scratchpad for banning stages and picking the one to play. State lives only while this screen is open; nothing is saved. */
@Composable
fun StageBansScreen() {
  // Sets are immutable, so we replace the whole value to trigger a redraw.
  var banned by remember { mutableStateOf(emptySet<String>()) }
  var selected by remember { mutableStateOf<String?>(null) }
  var selecting by remember { mutableStateOf(false) } // false = Ban mode, true = Select mode

  Column(modifier = Modifier.fillMaxSize().padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
    // weight(1f) makes the grid take all remaining height, pushing the button to the bottom.
    StagePicker(
      banned = banned,
      selected = selected,
      modifier = Modifier.weight(1f),
      onPick = { stage ->
        if (selecting) {
          // Can't pick a banned stage; tapping the selected one again clears it.
          if (stage.id !in banned) selected = if (selected == stage.id) null else stage.id
        } else {
          banned = if (stage.id in banned) banned - stage.id else banned + stage.id
          if (stage.id == selected) selected = null // banning the chosen stage un-chooses it
        }
      },
    )
    StageModeButton(selecting, onToggle = { selecting = !selecting }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
  }
}

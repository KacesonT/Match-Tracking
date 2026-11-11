package com.kacesontisdel.matchtracking.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.togetherWith
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kacesontisdel.matchtracking.data.STAGES
import com.kacesontisdel.matchtracking.data.Stage

private const val COLUMNS = 3
const val STAGE_ASPECT = 108f / 90f // stg_*.png are 108x90; matching it means nothing gets cropped
private val GAP = 8.dp
private val TILE_SHAPE = RoundedCornerShape(12.dp)
private val SELECTED_GREEN = Color(0xFF4CAF50) // bright enough to read on the dark theme and on stage art

// tween = fixed duration with no overshoot (the defaults are springs, which felt slow and bouncy).
private const val ANIM_MS = 150
private val POP_IN = fadeIn(tween(ANIM_MS)) + scaleIn(tween(ANIM_MS), initialScale = 0.6f)
private val POP_OUT = fadeOut(tween(ANIM_MS)) + scaleOut(tween(ANIM_MS), targetScale = 0.6f)

/** Ban/Select mode toggle: shows a red X in Ban mode, a green check in Select mode. Tapping swaps. */
@Composable
fun StageModeButton(selecting: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
  OutlinedButton(onClick = onToggle, modifier = modifier) {
    // AnimatedContent crossfades between the old and new icon whenever `selecting` flips.
    AnimatedContent(targetState = selecting, transitionSpec = { POP_IN togetherWith POP_OUT }) { sel ->
      if (sel) {
        Icon(Icons.Default.Check, contentDescription = "Select mode. Tap to switch to Ban mode", tint = SELECTED_GREEN, modifier = Modifier.size(28.dp))
      } else {
        Icon(Icons.Default.Close, contentDescription = "Ban mode. Tap to switch to Select mode", tint = Color.Red, modifier = Modifier.size(28.dp))
      }
    }
  }
}

/**
 * Three-column grid of the stages in STAGES; GameIcon shows the name as text until stg_<id>.png exists.
 * Banned stages get a red X on top, the [selected] stage gets a green check.
 * Plain rows, not a lazy grid: there are only 8 stages, and this way it sits inside scrolling screens with no fixed height.
 */
@Composable
fun StagePicker(
  onPick: (Stage) -> Unit,
  modifier: Modifier = Modifier,
  banned: Set<String> = emptySet(),
  selected: String? = null,
  horizontalPadding: Dp = 16.dp,
) {
  Column(modifier = modifier.padding(horizontal = horizontalPadding), verticalArrangement = Arrangement.spacedBy(GAP)) {
    STAGES.chunked(COLUMNS).forEach { row ->
      Row(horizontalArrangement = Arrangement.spacedBy(GAP)) {
        row.forEach { stage ->
          StageTile(stage, isBanned = stage.id in banned, isSelected = stage.id !in banned && stage.id == selected, onPick, Modifier.weight(1f))
        }
        // Empty slots keep the last row's tiles the same width as the rest.
        repeat(COLUMNS - row.size) { Spacer(Modifier.weight(1f)) }
      }
    }
  }
}

@Composable
private fun StageTile(stage: Stage, isBanned: Boolean, isSelected: Boolean, onPick: (Stage) -> Unit, modifier: Modifier) {
  // animate*AsState: when the target changes, the value glides there over a few frames instead of jumping.
  val borderColor by animateColorAsState(if (isSelected) SELECTED_GREEN else Color.Transparent, tween(ANIM_MS))
  val iconAlpha by animateFloatAsState(if (isBanned) 0.4f else 1f, tween(ANIM_MS))

  // Box stacks its children on top of each other: the icon first, then the overlay symbol.
  // clip comes before clickable so the press ripple is rounded too.
  Box(
    modifier = modifier
      .aspectRatio(STAGE_ASPECT)
      .clip(TILE_SHAPE)
      .border(3.dp, borderColor, TILE_SHAPE)
      .clickable { onPick(stage) },
    contentAlignment = Alignment.Center,
  ) {
    GameIcon(
      prefix = "stg",
      id = stage.id,
      name = stage.name,
      modifier = Modifier.matchParentSize().alpha(iconAlpha),
      contentScale = ContentScale.Crop, // tile and image share a shape, so this only fills rounding gaps
    )
    AnimatedVisibility(visible = isBanned, enter = POP_IN, exit = POP_OUT) {
      Icon(Icons.Default.Close, contentDescription = "Banned", tint = Color.Red, modifier = Modifier.size(48.dp))
    }
    AnimatedVisibility(visible = isSelected, enter = POP_IN, exit = POP_OUT) {
      Icon(Icons.Default.Check, contentDescription = "Selected", tint = SELECTED_GREEN, modifier = Modifier.size(48.dp))
    }
  }
}

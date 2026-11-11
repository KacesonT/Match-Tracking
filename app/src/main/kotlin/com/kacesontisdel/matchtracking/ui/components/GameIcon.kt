package com.kacesontisdel.matchtracking.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

/** Shows res/drawable/<prefix>_<id>.png ("chr" or "stg"), or [name] as text if that file is missing. */
@SuppressLint("DiscouragedApi") // getIdentifier is deliberate: icons are looked up by name at runtime
@Composable
fun GameIcon(
  prefix: String,
  id: String,
  name: String,
  modifier: Modifier = Modifier,
  contentScale: ContentScale = ContentScale.Fit,
) {
  val context = LocalContext.current
  // remember(...) = only redo the lookup if prefix/id change, not on every redraw.
  val resId = remember(prefix, id) { context.resources.getIdentifier("${prefix}_$id", "drawable", context.packageName) }
  if (resId != 0) {
    Image(painter = painterResource(resId), contentDescription = name, modifier = modifier, contentScale = contentScale)
  } else {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
      Text(
        text = name,
        style = MaterialTheme.typography.labelSmall,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

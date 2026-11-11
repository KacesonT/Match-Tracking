package com.kacesontisdel.matchtracking.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.kacesontisdel.matchtracking.R
import com.kacesontisdel.matchtracking.ui.backup.BackupScreen
import com.kacesontisdel.matchtracking.ui.logset.LogSetScreen
import com.kacesontisdel.matchtracking.ui.stagebans.StageBansScreen
import com.kacesontisdel.matchtracking.ui.stats.StatsScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
  // The back stack is a list of keys; the LAST one is the screen being shown.
  // It always starts with LogSetKey, so pressing back on any other screen returns there,
  // and pressing back on Log Set leaves the app.
  val backStack = rememberNavBackStack(LogSetKey)

  val drawerState = rememberDrawerState(DrawerValue.Closed)
  val scope = rememberCoroutineScope()

  val current = backStack.lastOrNull()
  val title = destinations.firstOrNull { it.key == current }?.label ?: ""

  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
      ModalDrawerSheet {
        Text(
          text = stringResource(R.string.app_name), // same name as the launcher label
          style = MaterialTheme.typography.titleLarge,
          modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp),
        )
        destinations.forEach { destination ->
          NavigationDrawerItem(
            label = { Text(destination.label) },
            selected = destination.key == current,
            onClick = {
              backStack.switchTo(destination.key)
              scope.launch { drawerState.close() }
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
          )
        }
      }
    },
  ) {
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text(title) },
          navigationIcon = {
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
              Icon(Icons.Default.Menu, contentDescription = "Open menu")
            }
          },
        )
      }
    ) { innerPadding ->
      NavDisplay(
        modifier = Modifier.padding(innerPadding),
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider =
          entryProvider {
            entry<LogSetKey> { LogSetScreen() }
            entry<StatsKey> { StatsScreen() }
            entry<StageBansKey> { StageBansScreen() }
            entry<BackupKey> { BackupScreen() }
          },
      )
    }
  }
}

// An "extension function": adds a new function to an existing type without editing that type.
// Here: keep only the first entry (LogSetKey), then put the chosen screen on top.
private fun MutableList<NavKey>.switchTo(key: NavKey) {
  while (size > 1) removeAt(lastIndex)
  if (key != first()) add(key)
}

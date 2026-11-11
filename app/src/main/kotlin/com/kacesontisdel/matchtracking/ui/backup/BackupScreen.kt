package com.kacesontisdel.matchtracking.ui.backup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun BackupScreen(viewModel: BackupViewModel = viewModel(factory = BackupViewModel.Factory)) {
  val message by viewModel.message.collectAsStateWithLifecycle()

  // The system file pickers: we ask, the user picks a file, and the result arrives in the lambda (null = cancelled).
  val exportLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
      uri?.let(viewModel::export)
    }
  val importLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> uri?.let(viewModel::import) }

  Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Text("Export saves all your sets to a JSON file. Import adds only sets you don't already have.")
    Button(onClick = { exportLauncher.launch("data-tracker-backup.json") }, modifier = Modifier.fillMaxWidth()) {
      Text("Export to file")
    }
    OutlinedButton(onClick = { importLauncher.launch(arrayOf("*/*")) }, modifier = Modifier.fillMaxWidth()) {
      Text("Import from file")
    }
    message?.let { Text(it) }
  }
}

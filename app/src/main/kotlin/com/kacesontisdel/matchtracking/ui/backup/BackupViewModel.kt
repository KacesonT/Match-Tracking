package com.kacesontisdel.matchtracking.ui.backup

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kacesontisdel.matchtracking.MatchTrackingApp
import com.kacesontisdel.matchtracking.data.SetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BackupViewModel(private val repository: SetRepository, private val resolver: ContentResolver) : ViewModel() {
  private val _message = MutableStateFlow<String?>(null)
  val message: StateFlow<String?> = _message.asStateFlow()

  /** [uri] is the file the user picked in the system "save as" dialog. */
  fun export(uri: Uri) = run("Export failed") {
    val json = repository.exportJson()
    withContext(Dispatchers.IO) {
      // "wt" = write, and truncate: overwriting an existing file must not leave old bytes at the end.
      resolver.openOutputStream(uri, "wt")!!.use { it.write(json.toByteArray()) }
    }
    "Exported ${json.length / 1024 + 1} KB."
  }

  fun import(uri: Uri) = run("Import failed") {
    val text = withContext(Dispatchers.IO) { resolver.openInputStream(uri)!!.use { it.readBytes().decodeToString() } }
    val result = repository.importJson(text)
    "Imported ${result.added} new set(s), skipped ${result.skipped} already saved."
  }

  // Runs [block] and shows its returned text; any exception becomes "<failure>: <reason>".
  private fun run(failure: String, block: suspend () -> String) {
    viewModelScope.launch {
      _message.value =
        try {
          block()
        } catch (e: Exception) {
          "$failure: ${e.message ?: e::class.simpleName}"
        }
    }
  }

  companion object {
    val Factory = viewModelFactory {
      initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MatchTrackingApp
        BackupViewModel(app.container.setRepository, app.contentResolver)
      }
    }
  }
}

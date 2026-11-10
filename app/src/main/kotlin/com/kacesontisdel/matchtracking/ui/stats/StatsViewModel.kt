package com.kacesontisdel.matchtracking.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kacesontisdel.matchtracking.MatchTrackingApp
import com.kacesontisdel.matchtracking.data.SetRepository
import com.kacesontisdel.matchtracking.data.db.StatRow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

/** Each tab is just a choice of which two grouping switches to hand to the stats query. */
enum class StatsTab(val label: String, val byStage: Boolean, val byOpponent: Boolean) {
  VsCharacter("vs Character", byStage = false, byOpponent = true),
  ByStage("By Stage", byStage = true, byOpponent = false),
  StageVsCharacter("Stage × Character", byStage = true, byOpponent = true),
}

@OptIn(ExperimentalCoroutinesApi::class)
class StatsViewModel(repository: SetRepository) : ViewModel() {
  private val _tab = MutableStateFlow(StatsTab.VsCharacter)
  val tab: StateFlow<StatsTab> = _tab.asStateFlow()

  /** null = all of my characters. */
  private val _myCharacterId = MutableStateFlow<String?>(null)
  val myCharacterId: StateFlow<String?> = _myCharacterId.asStateFlow()

  /** null = all opponent characters. */
  private val _opponentCharacterId = MutableStateFlow<String?>(null)
  val opponentCharacterId: StateFlow<String?> = _opponentCharacterId.asStateFlow()

  // Re-runs the query whenever the tab or a filter changes; flatMapLatest drops the previous query's subscription.
  val rows: StateFlow<List<StatRow>> =
    combine(_tab, _myCharacterId, _opponentCharacterId) { tab, mine, opp -> Triple(tab, mine, opp) }
      .flatMapLatest { (tab, mine, opp) -> repository.stats(tab.byStage, tab.byOpponent, mine, opp) }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

  fun selectTab(tab: StatsTab) { _tab.value = tab }
  fun filterByCharacter(id: String?) { _myCharacterId.value = id }
  fun filterByOpponent(id: String?) { _opponentCharacterId.value = id }

  companion object {
    val Factory = viewModelFactory {
      initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MatchTrackingApp
        StatsViewModel(app.container.setRepository)
      }
    }
  }
}

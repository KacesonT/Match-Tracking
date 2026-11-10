package com.kacesontisdel.matchtracking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kacesontisdel.matchtracking.ui.AppRoot
import com.kacesontisdel.matchtracking.ui.theme.MatchTrackingTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()
    // setContent is the bridge from the old Android world (Activity) to Compose.
    setContent { MatchTrackingTheme { AppRoot() } }
  }
}

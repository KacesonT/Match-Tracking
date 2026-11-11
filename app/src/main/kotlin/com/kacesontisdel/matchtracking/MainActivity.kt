package com.kacesontisdel.matchtracking

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kacesontisdel.matchtracking.ui.AppRoot
import com.kacesontisdel.matchtracking.ui.theme.MatchTrackingTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // The app is always dark, so ask for light status/nav bar icons even when the phone is in light mode.
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
      navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
    )
    // setContent is the bridge from the old Android world (Activity) to Compose.
    setContent { MatchTrackingTheme(darkTheme = true) { AppRoot() } }
  }
}

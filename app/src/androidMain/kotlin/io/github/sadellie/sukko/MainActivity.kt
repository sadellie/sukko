package io.github.sadellie.sukko

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.navigation3.runtime.rememberNavBackStack
import coil3.ImageLoader
import io.github.sadellie.sukko.core.common.filesPath
import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.ui.MainApp
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {
  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    setContent {
      MainApp(
        onLastRoutePop = { this.finish() },
        windowsSize = calculateWindowSizeClass(this),
        imageLoader = get<ImageLoader>(),
        filesDirPath = this.filesPath,
        backStack = rememberNavBackStack(CommonRoute.HomeRoute),
      )
    }
  }
}

package io.github.sadellie.sukko

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import dev.zacsweers.metro.Inject
import io.github.sadellie.sukko.core.routes.CommonRoute

@Inject
class MainActivity : ComponentActivity() {
  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    create(initialRoute = CommonRoute.HomeRoute) { super.onCreate(savedInstanceState) }
  }
}

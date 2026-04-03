package io.github.sadellie.sukko

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import io.github.sadellie.sukko.core.data.dataModule
import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.ui.MainApp
import io.github.sadellie.sukko.feature.editor.editorModule
import io.github.sadellie.sukko.feature.home.homeModule
import io.github.sadellie.sukko.feature.widget.widgetModule
import io.github.sadellie.sukko.feature.widgetinfo.widgetInfoModule
import kotlinx.browser.document
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import okio.Path.Companion.toPath
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
fun main() {
  setupKoin()
  val navBackStackConfig = SavedStateConfiguration {
    this.serializersModule = SerializersModule {
      polymorphic(NavKey::class) {
        subclass(CommonRoute.HomeRoute::class, CommonRoute.HomeRoute.serializer())
      }
    }
  }

  ComposeViewport(document.body!!) {
    val containerSize = LocalWindowInfo.current.containerSize
    val density = LocalDensity.current
    val windowSizeClass =
      remember(containerSize, density) {
        with(density) {
          WindowSizeClass.calculateFromSize(
            DpSize(containerSize.width.toDp(), containerSize.height.toDp())
          )
        }
      }
    MainApp(
      onLastRoutePop = {},
      windowsSize = windowSizeClass,
      imageLoader = ImageLoader.Builder(LocalPlatformContext.current).build(),
      filesDirPath = "".toPath(),
      backStack = rememberNavBackStack(navBackStackConfig, CommonRoute.HomeRoute),
    )
  }
}

private fun setupKoin() = startKoin {
  modules(homeModule, editorModule, widgetInfoModule, widgetModule, dataModule)
}

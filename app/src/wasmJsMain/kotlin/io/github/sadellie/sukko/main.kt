package io.github.sadellie.sukko

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
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
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import io.github.sadellie.sukko.core.data.DataBindings
import io.github.sadellie.sukko.core.routes.CommonRoute
import io.github.sadellie.sukko.core.routes.ui.MainApp
import io.github.sadellie.sukko.feature.home.homeNavigation
import kotlinx.browser.document
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import okio.Path.Companion.toPath

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
fun main() {
  val appGraph = createGraphFactory<WasmAppGraph.Factory>().create(DataBindings())
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
    CompositionLocalProvider(LocalMetroViewModelFactory provides appGraph.metroViewModelFactory) {
      MainApp(
        onLastRoutePop = {},
        windowsSize = windowSizeClass,
        imageLoader = ImageLoader.Builder(LocalPlatformContext.current).build(),
        filesDirPath = "".toPath(),
        backStack = rememberNavBackStack(navBackStackConfig, CommonRoute.HomeRoute),
        entries = { homeNavigation(onAddWidget = { /* TODO Not implemented yet */ }) },
      )
    }
  }
}

@DependencyGraph(AppScope::class)
interface WasmAppGraph : AppGraph {
  @DependencyGraph.Factory
  fun interface Factory {
    fun create(@Includes dataBindings: DataBindings): WasmAppGraph
  }
}

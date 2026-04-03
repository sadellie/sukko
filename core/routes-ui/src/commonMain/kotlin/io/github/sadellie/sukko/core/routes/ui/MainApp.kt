package io.github.sadellie.sukko.core.routes.ui

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import coil3.ImageLoader
import io.github.sadellie.sukko.core.designsystem.LocalFilesDirPath
import io.github.sadellie.sukko.core.designsystem.LocalImageLoader
import io.github.sadellie.sukko.core.designsystem.LocalWindowSize
import io.github.sadellie.sukko.core.routes.LocalNavigator
import io.github.sadellie.sukko.core.routes.Navigator
import io.github.sadellie.themmo.Themmo
import io.github.sadellie.themmo.rememberThemmoController
import okio.Path
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

@Composable
fun MainApp(
  onLastRoutePop: () -> Unit,
  windowsSize: WindowSizeClass,
  imageLoader: ImageLoader,
  filesDirPath: Path,
  backStack: NavBackStack<NavKey>,
) {
  val navigator = remember(backStack) { Navigator(backStack, onLastRoutePop) }
  CompositionLocalProvider(
    LocalWindowSize provides windowsSize,
    LocalImageLoader provides imageLoader,
    LocalFilesDirPath provides filesDirPath,
    LocalNavigator provides navigator,
  ) {
    val themmoController = rememberThemmoController()
    Themmo(themmoController = themmoController) { MainAppNav() }
  }
}

@OptIn(KoinExperimentalAPI::class)
@Composable
private fun MainAppNav() {
  NavDisplay(
    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer),
    backStack = LocalNavigator.current.backStack,
    entryDecorators =
      listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator(),
      ),
    entryProvider = koinEntryProvider(),
  )
}

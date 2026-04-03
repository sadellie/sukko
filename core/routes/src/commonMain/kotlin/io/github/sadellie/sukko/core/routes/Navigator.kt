package io.github.sadellie.sukko.core.routes

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

val LocalNavigator = staticCompositionLocalOf<Navigator> { error("No LocalNavigator provided") }

class Navigator(val backStack: NavBackStack<NavKey>, val onLastRoutePop: () -> Unit) {
  fun goTo(destination: NavKey) {
    if (destination != backStack.lastOrNull()) backStack.add(destination)
  }

  fun goBack() =
    if (backStack.size == 1) {
      onLastRoutePop()
      null
    } else {
      backStack.removeLastOrNull()
    }
}

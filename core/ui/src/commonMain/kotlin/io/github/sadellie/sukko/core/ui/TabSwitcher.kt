package io.github.sadellie.sukko.core.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingToolbarColors
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarScrollBehavior
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import google.material.design.symbols.Home
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_confirm
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TabsSwitcher(
  modifier: Modifier,
  tabs: List<Tab>,
  onClick: (Tab) -> Unit,
  selectedTabIndex: Int,
  scrollBehavior: FloatingToolbarScrollBehavior? = null,
  colors: FloatingToolbarColors =
    FloatingToolbarDefaults.standardFloatingToolbarColors(
      toolbarContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ),
) {
  HorizontalFloatingToolbar(
    modifier = modifier,
    expanded = true,
    colors = colors,
    scrollBehavior = scrollBehavior,
  ) {
    tabs.forEachIndexed { index, tab ->
      AnimatedContent(
        targetState = selectedTabIndex == index,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
      ) { isSelected ->
        val colors =
          if (isSelected) ButtonDefaults.filledTonalButtonColors()
          else ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
        Button(
          shapes = ButtonDefaults.shapes(),
          onClick = { if (!isSelected) onClick(tab) },
          contentPadding = ButtonDefaults.SmallContentPadding,
          colors = colors,
          modifier = Modifier.heightIn(ButtonDefaults.MinHeight),
        ) {
          tab.icon?.let { icon ->
            Icon(
              imageVector = icon,
              contentDescription = stringResource(tab.label),
              modifier = Modifier.size(ButtonDefaults.SmallIconSize),
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
          }
          Text(stringResource(tab.label))
        }
      }
    }
  }
}

interface Tab {
  val icon: ImageVector?
  val label: StringResource
}

@Composable
@Preview
private fun PreviewMainAppBar() {
  var selectedTabIndex by remember { mutableIntStateOf(2) }
  val tabs = remember {
    listOf(
      object : Tab {
        override val label: StringResource = Res.string.common_confirm
        override val icon: ImageVector = Symbols.Home
      },
      object : Tab {
        override val label: StringResource = Res.string.common_confirm
        override val icon: ImageVector = Symbols.Home
      },
      object : Tab {
        override val label: StringResource = Res.string.common_confirm
        override val icon = null
      },
    )
  }

  TabsSwitcher(
    modifier = Modifier,
    tabs = tabs,
    onClick = { selectedTabIndex = tabs.indexOf(it) },
    selectedTabIndex = selectedTabIndex,
  )
}

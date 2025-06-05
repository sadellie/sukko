package io.github.sadellie.sukko.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measured
import androidx.compose.ui.layout.VerticalAlignmentLine
import google.material.design.symbols.MoreVert
import google.material.design.symbols.Symbols

@Composable
fun DropDownMenuWithButton(
  modifier: Modifier = Modifier,
  content: @Composable DropDownMenuWithButtonScope.() -> Unit,
) {
  var showMenu by rememberSaveable { mutableStateOf(false) }
  Box(modifier = modifier.wrapContentSize(Alignment.TopStart)) {
    IconButton(
      onClick = { showMenu = true },
      shapes = IconButtonDefaults.shapes(),
      modifier =
        Modifier.size(
          IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Narrow)
        ),
    ) {
      Icon(
        imageVector = Symbols.MoreVert,
        contentDescription = null,
        modifier = Modifier.size(IconButtonDefaults.smallIconSize),
      )
    }
    DropdownMenu(
      expanded = showMenu,
      onDismissRequest = { showMenu = false },
      shape = MaterialTheme.shapes.medium,
      containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
      val scope = remember { DropDownMenuWithButtonScopeImpl({ showMenu = false }, this) }
      scope.content()
    }
  }
}

@Composable
fun DropDownMenuWithFilledTonalButton(
  modifier: Modifier = Modifier,
  content: @Composable DropDownMenuWithButtonScope.() -> Unit,
) {
  var showMenu by rememberSaveable { mutableStateOf(false) }
  Box(modifier = modifier.wrapContentSize(Alignment.TopStart)) {
    FilledTonalIconButton(
      onClick = { showMenu = true },
      shapes = IconButtonDefaults.shapes(),
      modifier =
        Modifier.size(
          IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Narrow)
        ),
    ) {
      Icon(
        imageVector = Symbols.MoreVert,
        contentDescription = null,
        modifier = Modifier.size(IconButtonDefaults.smallIconSize),
      )
    }
    DropdownMenu(
      expanded = showMenu,
      onDismissRequest = { showMenu = false },
      shape = MaterialTheme.shapes.medium,
      containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
      val scope = remember { DropDownMenuWithButtonScopeImpl({ showMenu = false }, this) }
      scope.content()
    }
  }
}

interface DropDownMenuWithButtonScope : ColumnScope {
  fun closeMenu()
}

class DropDownMenuWithButtonScopeImpl(
  private val onClose: () -> Unit,
  private val columnScope: ColumnScope,
) : DropDownMenuWithButtonScope {
  override fun closeMenu() = onClose()

  override fun Modifier.align(alignment: Alignment.Horizontal) =
    with(columnScope) { align(alignment) }

  override fun Modifier.alignBy(alignmentLine: VerticalAlignmentLine) =
    with(columnScope) { alignBy(alignmentLine) }

  override fun Modifier.alignBy(alignmentLineBlock: (Measured) -> Int) =
    with(columnScope) { alignBy(alignmentLineBlock) }

  override fun Modifier.weight(weight: Float, fill: Boolean): Modifier =
    with(columnScope) { weight(weight, fill) }
}

package io.github.sadellie.sukko.core.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import google.material.design.symbols.Close
import google.material.design.symbols.Symbols

@Composable
fun RemoveButton(onRemoveClick: () -> Unit, enabled: Boolean = true) {
  IconButton(
    modifier =
      Modifier.size(
        IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Narrow)
      ),
    onClick = onRemoveClick,
    shapes = IconButtonDefaults.shapes(),
    enabled = enabled,
  ) {
    Icon(
      imageVector = Symbols.Close,
      contentDescription = null,
      modifier = Modifier.size(IconButtonDefaults.smallIconSize),
    )
  }
}

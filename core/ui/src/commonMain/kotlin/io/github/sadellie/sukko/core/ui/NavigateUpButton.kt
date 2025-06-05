package io.github.sadellie.sukko.core.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import google.material.design.symbols.ArrowBack
import google.material.design.symbols.Symbols

/**
 * Button that is used in Top bars
 *
 * @param onClick Action to be called when button is clicked.
 */
@Composable
fun NavigateUpButton(onClick: () -> Unit, enabled: Boolean = true) {
  IconButton(
    onClick = onClick,
    enabled = enabled,
    shapes = IconButtonDefaults.shapes(),
    modifier =
      Modifier.size(
        IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Uniform)
      ),
  ) {
    Icon(
      imageVector = Symbols.ArrowBack,
      contentDescription = null,
      modifier = Modifier.size(IconButtonDefaults.smallIconSize),
    )
  }
}

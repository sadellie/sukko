package io.github.sadellie.sukko.feature.editor.selector.scripteditor

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import google.material.design.symbols.AddPhotoAlternate
import google.material.design.symbols.Symbols

@Composable
internal actual fun AddImageLinkButton(onAdd: (String) -> Unit) {
  IconButton(
    modifier =
      Modifier.size(
        IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Uniform)
      ),
    onClick = {},
    shapes = IconButtonDefaults.shapes(),
    enabled = false,
  ) {
    Icon(
      imageVector = Symbols.AddPhotoAlternate,
      contentDescription = null,
      modifier = Modifier.size(IconButtonDefaults.smallIconSize),
    )
  }
}

package io.github.sadellie.sukko.core.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import google.material.design.symbols.Add
import google.material.design.symbols.Symbols
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LargeButton(
  modifier: Modifier,
  onClick: () -> Unit,
  label: String,
  icon: ImageVector?,
  contentDescription: String?,
  enabled: Boolean = true,
) {
  Button(
    modifier = modifier.heightIn(ButtonDefaults.LargeContainerHeight),
    onClick = onClick,
    shapes = ButtonDefaults.shapesFor(ButtonDefaults.LargeContainerHeight),
    enabled = enabled,
  ) {
    if (icon != null) {
      Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = Modifier.size(ButtonDefaults.LargeIconSize),
      )
      Spacer(Modifier.width(ButtonDefaults.LargeIconSpacing))
    }

    Text(text = label, style = ButtonDefaults.textStyleFor(ButtonDefaults.LargeContainerHeight))
  }
}

@Composable
@Preview
private fun PreviewLargeButton() {
  LargeButton(
    modifier = Modifier,
    onClick = {},
    label = "Button",
    icon = Symbols.Add,
    contentDescription = null,
  )
}

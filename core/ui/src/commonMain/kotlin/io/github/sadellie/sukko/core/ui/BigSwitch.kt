package io.github.sadellie.sukko.core.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import google.material.design.symbols.Check
import google.material.design.symbols.Close
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.designsystem.theme.Sizes

@Composable
fun BigSwitch(
  modifier: Modifier = Modifier,
  label: String,
  onCheckedChange: () -> Unit,
  checked: Boolean,
) {
  Row(
    modifier =
      modifier
        .clip(MaterialTheme.shapes.extraLarge)
        .clickable(onClick = onCheckedChange)
        .background(MaterialTheme.colorScheme.primaryContainer)
        .padding(Sizes.large),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = label,
      color = MaterialTheme.colorScheme.onPrimaryContainer,
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.weight(1f),
    )
    SwitchWithCheckIcon(checked = checked, onCheckedChange = null)
  }
}

@Composable
fun SwitchWithCheckIcon(
  checked: Boolean,
  onCheckedChange: ((Boolean) -> Unit)?,
  enabled: Boolean = true,
) {
  Switch(
    checked = checked,
    onCheckedChange = onCheckedChange,
    enabled = enabled,
    thumbContent = {
      val icon = remember(checked) { if (checked) Symbols.Check else Symbols.Close }
      AnimatedContent(checked) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          modifier = Modifier.size(SwitchDefaults.IconSize),
        )
      }
    },
  )
}

@Composable
@Preview
private fun PreviewBigSwitch() {
  var checked by remember { mutableStateOf(false) }
  BigSwitch(label = "Big switch", onCheckedChange = { checked = !checked }, checked = checked)
}

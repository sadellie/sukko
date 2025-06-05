package io.github.sadellie.sukko.feature.editor.selector

import androidx.compose.runtime.Composable
import com.composables.core.ModalBottomSheetState

@Composable
expect fun AppSelectorSheet(
  state: ModalBottomSheetState,
  onValueSelected: (label: String, packageName: String) -> Unit,
  packageName: String?,
)

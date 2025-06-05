package io.github.sadellie.sukko.feature.editor.selector

import androidx.compose.runtime.Composable
import com.composables.core.ModalBottomSheetState
import io.github.sadellie.sukko.core.common.notReady

@Composable
actual fun AppSelectorSheet(
  state: ModalBottomSheetState,
  onValueSelected: (label: String, packageName: String) -> Unit,
  packageName: String?,
) {
  notReady
}

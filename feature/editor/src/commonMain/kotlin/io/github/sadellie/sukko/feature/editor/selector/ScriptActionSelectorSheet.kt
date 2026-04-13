package io.github.sadellie.sukko.feature.editor.selector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.composables.core.ModalBottomSheetState
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.ui.ModalBottomSheet2
import io.github.sadellie.sukko.core.ui.hide
import io.github.sadellie.sukko.feature.editor.selector.scripteditor.ScriptEditorSheetContent

@Composable
internal fun ScriptActionSelectorSheet(
  state: ModalBottomSheetState,
  onConfirm: (newScript: String) -> Unit,
  script: String,
  globals: Globals,
) {
  ModalBottomSheet2(state) {
    ScriptEditorSheetContent(
      initialInput = remember { script },
      globals = globals,
      enableGlobalOverrides = true,
      onDismiss = state::hide,
      onConfirm = onConfirm,
    )
  }
}

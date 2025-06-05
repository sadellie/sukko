package io.github.sadellie.sukko.feature.editor.selector

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.composables.core.ModalBottomSheetState
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.model.basic.GlobalValue
import io.github.sadellie.sukko.core.model.basic.Scriptable
import io.github.sadellie.sukko.core.ui.AlertDialogWithListItems
import io.github.sadellie.sukko.core.ui.ListItem2
import io.github.sadellie.sukko.core.ui.ModalBottomSheet2
import io.github.sadellie.sukko.core.ui.SheetContentWithButtons
import io.github.sadellie.sukko.core.ui.listedShape
import io.github.sadellie.sukko.core.ui.singleShape
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_cancel
import io.github.sadellie.sukko.resources.common_confirm
import io.github.sadellie.sukko.resources.editor_selector_input_mode
import io.github.sadellie.sukko.resources.editor_selector_input_mode_fixed
import io.github.sadellie.sukko.resources.editor_selector_input_mode_global
import io.github.sadellie.sukko.resources.editor_selector_input_mode_script
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun <T : InputMode> SelectorSheetTemplate(
  state: ModalBottomSheetState,
  inputModes: List<T>,
  currentInputMode: T,
  onInputModeUpdate: (T) -> Unit,
  content: @Composable (currentMode: T) -> Unit,
) {
  ModalBottomSheet2(state = state) {
    SelectorSheetTemplateContent(
      currentInputMode = currentInputMode,
      inputModes = inputModes,
      onInputModeUpdate = onInputModeUpdate,
      content = content,
    )
  }
}

@Composable
internal fun <T : InputMode> SelectorSheetTemplateContent(
  currentInputMode: T,
  inputModes: List<T>,
  onInputModeUpdate: (T) -> Unit,
  content: @Composable (currentMode: T) -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(Sizes.large)) {
    InputModeSelector(
      currentInputMode = currentInputMode,
      inputModes = inputModes,
      onInputModeUpdate = onInputModeUpdate,
    )

    Crossfade(targetState = currentInputMode, content = content)
  }
}

@Composable
internal fun <T> GlobalSelectorSheetContent(
  onDismiss: () -> Unit,
  onConfirm: (globalId: Long) -> Unit,
  initialGlobalId: Long?,
  globals: List<GlobalValue<T>>,
  dismissLabel: String = stringResource(Res.string.common_cancel),
  confirmLabel: String = stringResource(Res.string.common_confirm),
) {
  var selectedGlobalId by remember { mutableStateOf(initialGlobalId) }
  SheetContentWithButtons(
    onDismiss = onDismiss,
    onConfirm = { selectedGlobalId?.let { onConfirm(it) } },
    isConfirmButtonEnabled = selectedGlobalId != null,
    dismissLabel = dismissLabel,
    confirmLabel = confirmLabel,
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxWidth().padding(horizontal = Sizes.large),
      verticalArrangement = ListArrangement,
    ) {
      itemsIndexed(globals) { index, global ->
        ListItem2(
          modifier = Modifier.clickable { selectedGlobalId = global.id },
          leadingContent = { RadioButton(selectedGlobalId == global.id, onClick = null) },
          headlineContent = { Text(global.label) },
          shape = ListItemDefaults.listedShape(index, globals.size),
        )
      }
    }
  }
}

@Composable
internal fun <T : InputMode> InputModeSelector(
  currentInputMode: T,
  inputModes: List<T>,
  onInputModeUpdate: (T) -> Unit,
) {
  var showDialog by rememberSaveable { mutableStateOf(false) }
  ListItem2(
    headlineContent = { Text(stringResource(Res.string.editor_selector_input_mode)) },
    supportingContent = { Text(stringResource(currentInputMode.displayName)) },
    modifier =
      Modifier.padding(horizontal = Sizes.large).clip(MaterialTheme.shapes.large).clickable {
        showDialog = true
      },
    shape = ListItemDefaults.singleShape,
  )
  if (showDialog) {
    AlertDialogWithListItems(
      title = stringResource(Res.string.editor_selector_input_mode),
      onDismiss = { showDialog = false },
      items = inputModes,
      key = null,
      headlineText = { stringResource(it.displayName) },
      onClick = { onInputModeUpdate(it) },
    )
  }
}

internal interface InputMode {
  val displayName: StringResource
}

internal enum class DefaultInputMode(override val displayName: StringResource) : InputMode {
  FIXED(displayName = Res.string.editor_selector_input_mode_fixed),
  SCRIPT(displayName = Res.string.editor_selector_input_mode_script);

  companion object {
    /**
     * Select appropriate [DefaultInputMode] based on [scriptable]
     *
     * @param scriptable Initial value when opening selector
     * @return Initial [DefaultInputMode] for [scriptable]
     */
    fun initialMode(scriptable: Scriptable<*>) =
      when (scriptable) {
        is Scriptable.Script<*> -> SCRIPT
        is Scriptable.Fixed<*, *> -> FIXED
        else -> FIXED
      }
  }
}

internal enum class DefaultInputMode2(override val displayName: StringResource) : InputMode {
  FIXED(displayName = Res.string.editor_selector_input_mode_fixed),
  SCRIPT(displayName = Res.string.editor_selector_input_mode_script),
  GLOBAL(displayName = Res.string.editor_selector_input_mode_global);

  companion object {
    /**
     * Select appropriate [DefaultInputMode2] based on [scriptable]
     *
     * @param scriptable Initial value when opening selector
     * @return Initial [DefaultInputMode2] for [scriptable]
     */
    fun initialMode(scriptable: Scriptable<*>) =
      when (scriptable) {
        is Scriptable.Script<*> -> SCRIPT
        is Scriptable.Fixed<*, *> -> FIXED
        is Scriptable.Global<*> -> GLOBAL
      }
  }
}

@Preview
@Composable
private fun PreviewScriptableSheetTemplate() = Preview2 {
  val inputModes = remember { DefaultInputMode.entries }
  var currentMode by remember { mutableStateOf<InputMode>(DefaultInputMode.FIXED) }
  SelectorSheetTemplateContent(
    inputModes = inputModes,
    currentInputMode = currentMode,
    onInputModeUpdate = { currentMode = it },
    content = { inputMode -> Text("Input mode: $inputMode") },
  )
}

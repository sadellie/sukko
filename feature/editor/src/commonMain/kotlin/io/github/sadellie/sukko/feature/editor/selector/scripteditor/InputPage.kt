package io.github.sadellie.sukko.feature.editor.selector.scripteditor

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import google.material.design.symbols.Check
import google.material.design.symbols.Error
import google.material.design.symbols.Help
import google.material.design.symbols.Symbols
import google.material.design.symbols.Sync
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.script.ScriptException
import io.github.sadellie.sukko.core.ui.AlertDialogWithText
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_error
import io.github.sadellie.sukko.resources.common_loading
import io.github.sadellie.sukko.resources.editor_selector_script_disable_auto_reload_text
import io.github.sadellie.sukko.resources.editor_selector_script_disable_auto_reload_title
import io.github.sadellie.sukko.resources.editor_selector_script_docs_out_of_range_text
import io.github.sadellie.sukko.resources.editor_selector_script_docs_out_of_range_title
import io.github.sadellie.sukko.resources.editor_selector_script_enable_auto_reload_text
import io.github.sadellie.sukko.resources.editor_selector_script_enable_auto_reload_title
import io.github.sadellie.sukko.resources.editor_selector_script_loading_auto_reload_text
import io.github.sadellie.sukko.resources.editor_selector_script_loading_no_auto_reload_text
import io.github.sadellie.sukko.resources.editor_selector_script_no_errors
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun <T> InputPage(
  modifier: Modifier,
  textFieldState: TextFieldState,
  scriptEditorState: ScriptEditorState<T>,
  onAutoReloadChange: (newValue: Boolean) -> Unit,
  openDocs: () -> Unit,
  successPreview: @Composable (T) -> Unit,
) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Sizes.large)) {
    Row(
      modifier = Modifier.horizontalScroll(rememberScrollState()).align(Alignment.End),
      horizontalArrangement = Arrangement.spacedBy(Sizes.extraSmall),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      AddImageLinkButton(onAdd = textFieldState::insert)
      DocsButton(openDocs)
      AutoReloadButton(
        isAutoReloadEnabled = scriptEditorState.isAutoReloadEnabled,
        onClick = onAutoReloadChange,
      )
      AnimatedContent(targetState = scriptEditorState) { currentState ->
        when (currentState) {
          is ScriptEditorState.GenericError ->
            ErrorMessage(
              title = "Generic error",
              verboseText =
                currentState.throwable.message ?: stringResource(Res.string.common_error),
            )
          is ScriptEditorState.ScriptError ->
            ErrorMessage(
              title = "Script error",
              verboseText =
                currentState.throwable.message ?: stringResource(Res.string.common_error),
            )
          is ScriptEditorState.Success<T> -> successPreview(currentState.scriptResult)
          is ScriptEditorState.Loading<T> -> LoadingMessage(currentState.isAutoReloadEnabled)
        }
      }
    }

    ScriptTextField(modifier = Modifier.weight(1f).fillMaxSize(), textFieldState = textFieldState)
  }
}

@Composable
private fun ScriptTextField(modifier: Modifier, textFieldState: TextFieldState) {
  val scrollState = rememberScrollState()
  val textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
  BasicTextField(
    scrollState = scrollState,
    state = textFieldState,
    modifier = modifier,
    textStyle = textStyle.copy(color = MaterialTheme.colorScheme.onSurface),
    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
    decorator = { textField ->
      Row(
        modifier =
          Modifier.clip(MaterialTheme.shapes.medium)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
      ) {
        Column(
          modifier =
            Modifier.verticalScroll(scrollState)
              .background(MaterialTheme.colorScheme.surfaceContainerLowest)
              .fillMaxHeight()
              .padding(Sizes.small)
        ) {
          repeat(textFieldState.text.lines().count()) { Text(text = "${it+1}", style = textStyle) }
        }
        Box(
          modifier =
            Modifier.horizontalScroll(rememberScrollState()).padding(Sizes.small).weight(1f)
        ) {
          textField()
        }
      }
    },
  )
}

@Composable internal expect fun AddImageLinkButton(onAdd: (String) -> Unit)

@Composable
private fun DocsButton(onClick: () -> Unit) {
  IconButton(
    modifier =
      Modifier.size(
        IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Uniform)
      ),
    onClick = onClick,
    shapes = IconButtonDefaults.shapes(),
  ) {
    Icon(
      imageVector = Symbols.Help,
      contentDescription = null,
      modifier = Modifier.size(IconButtonDefaults.smallIconSize),
    )
  }
}

@Composable
private fun AutoReloadButton(isAutoReloadEnabled: Boolean, onClick: (newValue: Boolean) -> Unit) {
  var showDialog by rememberSaveable { mutableStateOf(false) }
  IconToggleButton(
    modifier =
      Modifier.size(
        IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Uniform)
      ),
    checked = isAutoReloadEnabled,
    onCheckedChange = { showDialog = true },
  ) {
    Icon(
      imageVector = Symbols.Sync,
      contentDescription = null,
      modifier = Modifier.size(IconButtonDefaults.smallIconSize),
    )
  }

  if (showDialog && isAutoReloadEnabled) {
    AlertDialogWithText(
      onDismiss = { showDialog = false },
      onConfirm = { onClick(false) },
      title = stringResource(Res.string.editor_selector_script_disable_auto_reload_title),
      text = stringResource(Res.string.editor_selector_script_disable_auto_reload_text),
    )
  }
  if (showDialog && !isAutoReloadEnabled) {
    AlertDialogWithText(
      onDismiss = { showDialog = false },
      onConfirm = { onClick(true) },
      title = stringResource(Res.string.editor_selector_script_enable_auto_reload_title),
      text = stringResource(Res.string.editor_selector_script_enable_auto_reload_text),
    )
  }
}

@Composable
internal fun SuccessMessage(text: String) {
  ExpandableMessage(
    title = stringResource(Res.string.editor_selector_script_no_errors),
    text = text,
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    leadingIcon = Symbols.Check,
  )
}

@Composable
private fun LoadingMessage(isAutoReloadEnabled: Boolean) {
  ExpandableMessage(
    title = stringResource(Res.string.common_loading),
    text =
      stringResource(
        if (isAutoReloadEnabled) Res.string.editor_selector_script_loading_auto_reload_text
        else Res.string.editor_selector_script_loading_no_auto_reload_text
      ),
    containerColor = Color.Unspecified,
    contentColor = Color.Unspecified,
    content = {
      CircularProgressIndicator(
        modifier = Modifier.size(IconButtonDefaults.smallIconSize),
        strokeWidth = 2.dp,
      )
    },
  )
}

@Composable
internal fun <T : Comparable<T>> OutOfRangeErrorMessage(range: ClosedRange<T>) {
  ErrorMessage(
    title = stringResource(Res.string.editor_selector_script_docs_out_of_range_title),
    verboseText =
      stringResource(
        Res.string.editor_selector_script_docs_out_of_range_text,
        range.start.toString(),
        range.endInclusive.toString(),
      ),
  )
}

@Composable
internal fun ErrorMessage(title: String, verboseText: String) {
  ExpandableMessage(
    title = title,
    text = verboseText,
    containerColor = MaterialTheme.colorScheme.errorContainer,
    contentColor = MaterialTheme.colorScheme.onErrorContainer,
    leadingIcon = Symbols.Error,
  )
}

@Composable
private fun ExpandableMessage(
  title: String,
  text: String,
  containerColor: Color,
  contentColor: Color,
  leadingIcon: ImageVector,
) {
  ExpandableMessage(
    title = title,
    text = text,
    containerColor = containerColor,
    contentColor = contentColor,
    content = {
      Icon(
        imageVector = leadingIcon,
        contentDescription = null,
        modifier = Modifier.size(IconButtonDefaults.smallIconSize),
      )
    },
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpandableMessage(
  title: String,
  text: String,
  containerColor: Color,
  contentColor: Color,
  content: @Composable () -> Unit,
) {
  val tooltipState = rememberTooltipState(isPersistent = true)
  val coroutineScope = rememberCoroutineScope()
  TooltipBox(
    positionProvider =
      TooltipDefaults.rememberTooltipPositionProvider(positioning = TooltipAnchorPosition.Below),
    tooltip = { RichTooltip(title = { Text(text = title) }) { Text(text) } },
    state = tooltipState,
  ) {
    FilledIconButton(
      modifier =
        Modifier.size(
          IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Uniform)
        ),
      onClick = { coroutineScope.launch { tooltipState.show() } },
      shapes = IconButtonDefaults.shapes(),
      colors =
        IconButtonDefaults.iconButtonColors(
          containerColor = containerColor,
          contentColor = contentColor,
        ),
      content = content,
    )
  }
}

@Composable
@Preview
private fun PreviewInputPageContent(
  @PreviewParameter(ScriptEditorPreviewCollection::class)
  scriptEditorState: ScriptEditorState<String>
) = Preview2 {
  Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
    InputPage(
      modifier = Modifier.height(400.dp).fillMaxWidth().padding(Sizes.large),
      scriptEditorState = scriptEditorState,
      onAutoReloadChange = {},
      textFieldState = rememberTextFieldState("this is a test\nsecond line"),
      openDocs = {},
    ) {
      SuccessMessage(it)
    }
  }
}

private class ScriptEditorPreviewCollection(
  override val values: Sequence<ScriptEditorState<String>> =
    sequenceOf(
      ScriptEditorState.Loading(false),
      ScriptEditorState.Loading(true),
      ScriptEditorState.GenericError(IndexOutOfBoundsException("Out of bound"), false),
      ScriptEditorState.ScriptError(ScriptException.VariableNameClash("test"), false),
      ScriptEditorState.Success("Test value (OK)", false),
    )
) : PreviewParameterProvider<ScriptEditorState<String>?>

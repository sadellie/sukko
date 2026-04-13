package io.github.sadellie.sukko.feature.editor.selector.scripteditor

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import google.material.design.symbols.Check
import google.material.design.symbols.Error
import google.material.design.symbols.Help
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.data.script.ScriptException
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.ui.EmptyScreen
import io.github.sadellie.sukko.core.ui.ListItem2
import io.github.sadellie.sukko.core.ui.singleShapes
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_error
import io.github.sadellie.sukko.resources.common_loading
import io.github.sadellie.sukko.resources.editor_selector_script_loading_auto_reload_text
import io.github.sadellie.sukko.resources.editor_selector_script_no_errors_text
import io.github.sadellie.sukko.resources.editor_selector_script_no_errors_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun InputPage(
  modifier: Modifier,
  openDocs: () -> Unit,
  uiState: InputPageUIState?,
  observeInput: suspend () -> Unit,
) {
  LaunchedEffect(Unit) { observeInput() }
  if (uiState == null) {
    EmptyScreen()
  } else {
    InputPageContent(modifier = modifier, uiState = uiState, openDocs = openDocs)
  }
}

@Composable
private fun InputPageContent(modifier: Modifier, uiState: InputPageUIState, openDocs: () -> Unit) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Sizes.large)) {
    Row(
      modifier = Modifier.horizontalScroll(rememberScrollState()).align(Alignment.End),
      horizontalArrangement = Arrangement.spacedBy(Sizes.extraSmall),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      AddImageLinkButton(onAdd = uiState.input::insert)
      DocsButton(openDocs)
    }
    AnimatedContent(targetState = uiState.result) { currentState ->
      when (currentState) {
        is ScriptResult.GenericError ->
          ErrorMessage(
            title = "Generic error",
            verboseText = currentState.throwable.message ?: stringResource(Res.string.common_error),
          )
        is ScriptResult.ScriptError ->
          ErrorMessage(
            title = "Script error",
            verboseText = currentState.throwable.message ?: stringResource(Res.string.common_error),
          )
        is ScriptResult.Success -> SuccessMessage(text = currentState.scriptResult)

        ScriptResult.Loading -> LoadingMessage()
      }
    }
    ScriptTextField(modifier = Modifier.weight(1f).fillMaxSize(), textFieldState = uiState.input)
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
internal fun SuccessMessage(text: String?) {
  ExpandableMessage(
    title = stringResource(Res.string.editor_selector_script_no_errors_title),
    text = text ?: stringResource(Res.string.editor_selector_script_no_errors_text),
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    leadingIcon = Symbols.Check,
  )
}

@Composable
private fun LoadingMessage() {
  ExpandableMessage(
    title = stringResource(Res.string.common_loading),
    text = stringResource(Res.string.editor_selector_script_loading_auto_reload_text),
    containerColor = Color.Unspecified,
    contentColor = Color.Unspecified,
    leadingContent = {
      CircularProgressIndicator(
        modifier = Modifier.size(IconButtonDefaults.smallIconSize),
        strokeWidth = 2.dp,
      )
    },
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
    leadingContent = {
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
  leadingContent: @Composable () -> Unit,
) {
  var expandedValue by rememberSaveable { mutableStateOf(false) }
  val interactionSource = remember { MutableInteractionSource() }
  AnimatedContent(expandedValue) { isExpanded ->
    ListItem2(
      interactionSource = interactionSource,
      onClick = { expandedValue = !isExpanded },
      shapes = ListItemDefaults.singleShapes,
      leadingContent = leadingContent,
      overlineContent = { Text(title) },
      content = {
        Text(
          text = text,
          maxLines = if (isExpanded) Int.MAX_VALUE else 1,
          overflow = TextOverflow.Ellipsis,
          onTextLayout = { it.hasVisualOverflow },
        )
      },
      colors = ListItemDefaults.colors(containerColor = containerColor, contentColor = contentColor),
    )
  }
}

internal data class InputPageUIState(val input: TextFieldState, val result: ScriptResult)

@Composable
@Preview
private fun PreviewInputPageContent(
  @PreviewParameter(ScriptEditorPreviewCollection::class) scriptResult: ScriptResult
) = Preview2 {
  Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
    InputPageContent(
      modifier = Modifier.height(400.dp).fillMaxWidth().padding(Sizes.large),
      openDocs = {},
      uiState =
        remember {
          InputPageUIState(
            input = TextFieldState("this is a test\nsecond line"),
            result = scriptResult,
          )
        },
    )
  }
}

private class ScriptEditorPreviewCollection(
  override val values: Sequence<ScriptResult> =
    sequenceOf(
      ScriptResult.Loading,
      ScriptResult.GenericError(IndexOutOfBoundsException("Out of bounds")),
      ScriptResult.ScriptError(ScriptException.VariableNameClash("test")),
      ScriptResult.Success("Test value (OK)"),
      ScriptResult.Success(null),
    )
) : PreviewParameterProvider<ScriptResult>

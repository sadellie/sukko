package io.github.sadellie.sukko.core.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.material3.TextFieldLabelScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density

@Composable
fun SukkoOutlinedTextField(
  state: TextFieldState,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  readOnly: Boolean = false,
  textStyle: TextStyle = LocalTextStyle.current,
  labelPosition: TextFieldLabelPosition = TextFieldLabelPosition.Attached(),
  label: @Composable (TextFieldLabelScope.() -> Unit)? = null,
  placeholder: @Composable (() -> Unit)? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  prefix: @Composable (() -> Unit)? = null,
  suffix: @Composable (() -> Unit)? = null,
  supportingText: @Composable (() -> Unit)? = null,
  isError: Boolean = false,
  inputTransformation: InputTransformation? = null,
  outputTransformation: OutputTransformation? = null,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  onKeyboardAction: KeyboardActionHandler? = null,
  lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
  onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
  scrollState: ScrollState = rememberScrollState(),
  colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
  contentPadding: PaddingValues = OutlinedTextFieldDefaults.contentPadding(),
  interactionSource: MutableInteractionSource? = null,
) =
  OutlinedTextField(
    state = state,
    modifier = modifier,
    enabled = enabled,
    readOnly = readOnly,
    textStyle = textStyle,
    labelPosition = labelPosition,
    label = label,
    placeholder = placeholder,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    prefix = prefix,
    suffix = suffix,
    supportingText = supportingText,
    isError = isError,
    inputTransformation = inputTransformation,
    outputTransformation = outputTransformation,
    keyboardOptions = keyboardOptions,
    onKeyboardAction = onKeyboardAction,
    lineLimits = lineLimits,
    onTextLayout = onTextLayout,
    scrollState = scrollState,
    shape = MaterialTheme.shapes.medium,
    colors = colors,
    contentPadding = contentPadding,
    interactionSource = interactionSource,
  )

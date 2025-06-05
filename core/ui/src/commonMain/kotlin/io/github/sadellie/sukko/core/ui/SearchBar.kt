package io.github.sadellie.sukko.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import google.material.design.symbols.ArrowBack
import google.material.design.symbols.Close
import google.material.design.symbols.Search
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_search_text_field_placeholder
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
  modifier: Modifier = Modifier,
  state: TextFieldState,
  navigateUp: () -> Unit,
  focusManager: FocusManager = LocalFocusManager.current,
  onSearch: () -> Unit = { focusManager.clearFocus() },
  focusRequester: FocusRequester = remember { FocusRequester() },
  leadingIcon: @Composable () -> Unit = { NavigateButton(navigateUp) },
  trailingIcon: @Composable () -> Unit = { SearchButton(focusManager::clearFocus) },
  scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
  placeholder: String = stringResource(Res.string.common_search_text_field_placeholder),
) {
  val notEmpty = remember(state.text) { state.text.isNotEmpty() }
  LaunchedEffect(Unit) { focusRequester.requestFocus() }
  LaunchedEffect(scrollBehavior.state.overlappedFraction) {
    if (scrollBehavior.state.collapsedFraction > COLLAPSED_TOP_BAR_THRESHOLD)
      focusManager.clearFocus()
  }
  BackHandler(notEmpty, state::clearText)

  val heightOffsetLimit =
    with(LocalDensity.current) { -(SearchBarTokens.UnittoSearchBarFullHeight).toPx() }
  SideEffect {
    if (scrollBehavior.state.heightOffsetLimit != heightOffsetLimit) {
      scrollBehavior.state.heightOffsetLimit = heightOffsetLimit
    }
  }
  val height =
    LocalDensity.current.run {
      SearchBarTokens.UnittoSearchBarFullHeight + scrollBehavior.state.heightOffset.toDp()
    }

  Box(
    modifier = modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets).height(height),
    contentAlignment = Alignment.Center,
  ) {
    Row(
      modifier =
        Modifier.offset { IntOffset(x = 0, y = scrollBehavior.state.heightOffset.roundToInt()) }
          .requiredHeight(SearchBarTokens.UnittoSearchBarHeight)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surfaceContainerHighest)
          .fillMaxWidth()
          .padding(horizontal = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
        if (notEmpty) NavigateButton(state::clearText) else leadingIcon()

        SearchTextField(
          modifier = Modifier.focusRequester(focusRequester).fillMaxWidth().weight(1f),
          state = state,
          placeholder = placeholder,
          onSearch = onSearch,
        )

        ClearButton(notEmpty, state::clearText)

        trailingIcon()
      }
    }
  }
}

@Composable
private fun SearchTextField(
  modifier: Modifier,
  state: TextFieldState,
  placeholder: String,
  onSearch: () -> Unit,
) {
  BasicTextField(
    modifier = modifier,
    state = state,
    lineLimits = TextFieldLineLimits.SingleLine,
    textStyle =
      MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    onKeyboardAction = { onSearch() },
    decorator = { innerTextField ->
      innerTextField()
      // Showing placeholder only when there is query is empty
      state.text.ifEmpty {
        Text(
          text = placeholder,
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    },
  )
}

@Composable
private fun SearchButton(onClick: () -> Unit) {
  IconButton(onClick) { Icon(imageVector = Symbols.Search, contentDescription = null) }
}

@Composable
private fun NavigateButton(onClick: () -> Unit) {
  IconButton(onClick) { Icon(imageVector = Symbols.ArrowBack, contentDescription = null) }
}

@Composable
private fun ClearButton(visible: Boolean, onClick: () -> Unit) {
  AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
    IconButton(onClick) { Icon(imageVector = Symbols.Close, contentDescription = null) }
  }
}

private object SearchBarTokens {
  val UnittoSearchBarHeight = 56.dp
  val UnittoSearchBarVerticalPadding = 8.dp
  val UnittoSearchBarFullHeight = UnittoSearchBarHeight + UnittoSearchBarVerticalPadding * 2
}

private const val COLLAPSED_TOP_BAR_THRESHOLD = 0.5f

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewSearchBar() {
  SearchBar(
    state = TextFieldState("test"),
    navigateUp = {},
    scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewSearchBarEmpty() {
  SearchBar(
    state = TextFieldState(),
    navigateUp = {},
    scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
  )
}

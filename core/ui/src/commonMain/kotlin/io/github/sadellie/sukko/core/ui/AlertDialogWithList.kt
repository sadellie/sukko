package io.github.sadellie.sukko.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_cancel
import io.github.sadellie.sukko.resources.common_confirm
import org.jetbrains.compose.resources.stringResource

/** [onClick] will call [onDismiss] */
@Composable
fun <T> AlertDialogWithRadioItems(
  title: String,
  onDismiss: () -> Unit,
  items: List<T>,
  key: ((index: Int, item: T) -> Any)?,
  headlineText: @Composable (T) -> String,
  isSelected: (T) -> Boolean,
  onClick: (T) -> Unit,
  listState: LazyListState = rememberLazyListState(),
) {
  AlertDialogWithList(title = title, onDismiss = onDismiss, listState = listState) {
    itemsIndexed(items = items, key = key) { _, item ->
      ListItem2(
        content = { Text(text = headlineText(item)) },
        leadingContent = {
          RadioButton(
            selected = remember(isSelected) { isSelected(item) },
            onClick = {
              onClick(item)
              onDismiss()
            },
          )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        shapes = ListItemDefaults.middleShapes,
        onClick = {
          onClick(item)
          onDismiss()
        },
      )
    }
  }
}

/** [onClick] will call [onDismiss] */
@Composable
fun <T> AlertDialogWithListItems(
  title: String,
  onDismiss: () -> Unit,
  items: List<T>,
  key: ((index: Int, item: T) -> Any)?,
  headlineText: @Composable (T) -> String,
  onClick: (T) -> Unit,
  listState: LazyListState = rememberLazyListState(),
) {
  AlertDialogWithList(title = title, onDismiss = onDismiss, listState = listState) {
    itemsIndexed(items = items, key = key) { _, item ->
      ListItem2(
        content = { Text(text = headlineText(item)) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        shapes = ListItemDefaults.middleShapes,
        onClick = {
          onClick(item)
          onDismiss()
        },
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogWithList(
  title: String,
  onDismiss: () -> Unit,
  listState: LazyListState = rememberLazyListState(),
  content: LazyListScope.() -> Unit,
) {
  BasicAlertDialog(onDismissRequest = onDismiss) {
    AlertDialogContent(
      modifier = Modifier,
      title = title,
      listState = listState,
      onDismiss = onDismiss,
      content = content,
    )
  }
}

/** [onDismiss] is always called after [onConfirm] */
@Composable
fun AlertDialogWithTextField(
  modifier: Modifier = Modifier,
  onDismiss: () -> Unit,
  onConfirm: (String) -> Unit,
  icon: ImageVector? = null,
  isConfirmButtonEnabled: (String) -> Boolean = { it.isNotBlank() },
  dismissButtonLabel: String = stringResource(Res.string.common_cancel),
  confirmButtonLabel: String = stringResource(Res.string.common_confirm),
  title: String,
  textFieldState: TextFieldState = rememberTextFieldState(),
  textFieldLabel: String? = null,
  additionalContent: (@Composable () -> Unit)? = null,
) {
  AlertDialog(
    modifier = modifier,
    icon = icon?.let { { Icon(icon, contentDescription = null) } },
    title = { Text(title) },
    text = {
      Column {
        SukkoOutlinedTextField(
          state = textFieldState,
          modifier = Modifier.fillMaxWidth(),
          label = textFieldLabel?.let { { Text(it) } },
          lineLimits = TextFieldLineLimits.SingleLine,
        )
        additionalContent?.invoke()
      }
    },
    onDismissRequest = onDismiss,
    dismissButton = {
      TextButton(onClick = onDismiss, shapes = ButtonDefaults.shapes()) { Text(dismissButtonLabel) }
    },
    confirmButton = {
      Button(
        onClick = {
          onConfirm(textFieldState.text.toString())
          onDismiss()
        },
        enabled = isConfirmButtonEnabled(textFieldState.text.toString()),
        shapes = ButtonDefaults.shapes(),
      ) {
        Text(confirmButtonLabel)
      }
    },
  )
}

/**
 * [onDismiss] is called when clicking a dismiss button. [onDismissDialog] is called when clicking
 * outside this dialog or confirm button. By default [onDismissDialog] uses [onDismiss] callback.
 */
@Composable
fun AlertDialogWithText(
  modifier: Modifier = Modifier,
  onDismiss: () -> Unit,
  onDismissDialog: () -> Unit = onDismiss,
  onConfirm: () -> Unit,
  isConfirmButtonEnabled: () -> Boolean = { true },
  dismissButtonLabel: String = stringResource(Res.string.common_cancel),
  confirmButtonLabel: String = stringResource(Res.string.common_confirm),
  title: String,
  text: String,
  icon: ImageVector? = null,
) {
  AlertDialog(
    modifier = modifier,
    icon = icon?.let { { Icon(icon, contentDescription = null) } },
    title = { Text(title) },
    text = { Text(text) },
    onDismissRequest = onDismissDialog,
    dismissButton = {
      TextButton(onDismiss, shapes = ButtonDefaults.shapes()) { Text(dismissButtonLabel) }
    },
    confirmButton = {
      Button(
        onClick = {
          onConfirm()
          onDismissDialog()
        },
        enabled = isConfirmButtonEnabled(),
        shapes = ButtonDefaults.shapes(),
      ) {
        Text(confirmButtonLabel)
      }
    },
  )
}

@Composable
private fun AlertDialogContent(
  modifier: Modifier,
  title: String,
  listState: LazyListState,
  onDismiss: () -> Unit,
  content: LazyListScope.() -> Unit,
) {
  Column(
    modifier
      .widthIn(280.dp, 560.dp)
      .clip(AlertDialogDefaults.shape)
      .background(AlertDialogDefaults.containerColor)
  ) {
    Text(
      modifier =
        Modifier.fillMaxWidth().padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp),
      text = title,
      style = MaterialTheme.typography.headlineSmall,
      color = MaterialTheme.colorScheme.onSurface,
    )
    if (listState.canScrollBackward) {
      HorizontalDivider()
    }
    LazyColumn(
      modifier = Modifier.fillMaxWidth().weight(1f, false),
      state = listState,
      content = content,
    )
    if (listState.canScrollForward) {
      HorizontalDivider()
    }
    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.BottomEnd) {
      TextButton(onClick = onDismiss, shapes = ButtonDefaults.shapes()) {
        Text(stringResource(Res.string.common_cancel))
      }
    }
  }
}

@Preview
@Composable
private fun PreviewAlertDialogWithRadioItems() {
  AlertDialogWithRadioItems(
    title = "Alert dialog",
    onDismiss = {},
    items = remember { List(5) { "Item $it" } },
    key = null,
    headlineText = { it },
    isSelected = { it == "Item 2" },
    onClick = {},
  )
}

@Preview
@Composable
private fun PreviewAlertDialogWithListItems() {
  AlertDialogWithListItems(
    title = "Alert dialog",
    onDismiss = {},
    items = remember { List(5) { "Item $it" } },
    key = null,
    headlineText = { it },
    onClick = {},
  )
}

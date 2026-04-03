package io.github.sadellie.sukko.core.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.core.DragIndication
import com.composables.core.ModalBottomSheet
import com.composables.core.ModalBottomSheetState
import com.composables.core.Scrim
import com.composables.core.Sheet
import com.composables.core.SheetDetent
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_cancel
import io.github.sadellie.sukko.resources.common_confirm
import org.jetbrains.compose.resources.stringResource

/** [onDismiss] is always called after [onConfirm] */
@Composable
fun ModalBottomSheetWithButtons(
  state: ModalBottomSheetState,
  onDismiss: () -> Unit = state::hide,
  onConfirm: (() -> Unit)? = null,
  isConfirmButtonEnabled: Boolean = true,
  content: @Composable () -> Unit,
) {
  ModalBottomSheet2(state) {
    SheetContentWithButtons(
      onDismiss = onDismiss,
      onConfirm = onConfirm,
      isConfirmButtonEnabled = isConfirmButtonEnabled,
      sheetContent = content,
    )
  }
}

/** [onDismiss] is always called after [onClick] */
@Composable
fun <T> ModalBottomSheetWithItems(
  state: ModalBottomSheetState,
  onClick: (T) -> Unit,
  onDismiss: () -> Unit = state::hide,
  key: (Int, T) -> Any = { index, _ -> index },
  headlineText: @Composable (T) -> String,
  supportText: (@Composable (T) -> String)? = null,
  leadingContent: (@Composable (T) -> Unit)? = null,
  items: List<T>,
) {
  ModalBottomSheetWithButtons(state, onDismiss) {
    LazyColumn(
      modifier = Modifier.padding(horizontal = Sizes.large),
      verticalArrangement = ListArrangement,
    ) {
      itemsIndexed(items = items, key = key) { index, item ->
        ListItem2(
          onClick = {
            onClick(item)
            onDismiss()
          },
          shapes = ListItemDefaults.listedShapes(index, items.size),
          content = { Text(headlineText(item)) },
          supportingContent = supportText?.let { { Text(supportText(item)) } },
          leadingContent = leadingContent?.let { { leadingContent(item) } },
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheet2(state: ModalBottomSheetState, sheetContent: @Composable () -> Unit) {
  ModalBottomSheet(state = state) {
    Scrim(
      enter = fadeIn(),
      exit = fadeOut(),
      scrimColor = MaterialTheme.colorScheme.scrim.copy(SCRIM_ALPHA),
    )
    Sheet(
      modifier =
        Modifier.statusBarsPadding()
          .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
          .background(MaterialTheme.colorScheme.surfaceContainer)
          .widthIn(max = 640.dp)
          .heightIn(max = 1200.dp)
          .fillMaxWidth()
          .imePadding()
          .navigationBarsPadding()
    ) {
      Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          DragIndication(
            modifier =
              Modifier.padding(vertical = 22.dp)
                .background(MaterialTheme.colorScheme.onSurfaceVariant, CircleShape)
                .width(32.dp)
                .height(4.dp)
          )

          sheetContent()
        }
      }
    }
  }
}

/**
 * A sheet content template with buttons to select or dismiss.
 *
 * @param onDismiss will be called after [onConfirm]
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SheetContentWithButtons(
  onDismiss: () -> Unit,
  onConfirm: (() -> Unit)? = null,
  dismissLabel: String = stringResource(Res.string.common_cancel),
  confirmLabel: String = stringResource(Res.string.common_confirm),
  isConfirmButtonEnabled: Boolean = true,
  sheetContent: @Composable () -> Unit,
) {
  Column {
    Box(modifier = Modifier.weight(1f, false)) { sheetContent() }
    Row(
      modifier = Modifier.fillMaxWidth().padding(Sizes.large),
      horizontalArrangement = Arrangement.End,
    ) {
      TextButton(onClick = onDismiss, shapes = ButtonDefaults.shapes()) { Text(dismissLabel) }
      if (onConfirm != null) {
        Spacer(Modifier.weight(1f))
        Button(
          onClick = {
            onConfirm()
            onDismiss()
          },
          shapes = ButtonDefaults.shapes(),
          enabled = isConfirmButtonEnabled,
        ) {
          Text(confirmLabel)
        }
      }
    }
  }
}

fun ModalBottomSheetState.hide() {
  this.targetDetent = SheetDetent.Hidden
}

fun ModalBottomSheetState.expand() {
  this.targetDetent = SheetDetent.FullyExpanded
}

private const val SCRIM_ALPHA = 0.6f

@Preview
@Composable
private fun PreviewSelectorSheetContentTemplate() {
  SheetContentWithButtons(onDismiss = {}, sheetContent = { Text("Content") })
}

@Preview
@Composable
private fun PreviewSelectorSheetContentTemplate2() {
  SheetContentWithButtons(
    onDismiss = {},
    onConfirm = {},
    isConfirmButtonEnabled = true,
    sheetContent = { Text("Content") },
  )
}

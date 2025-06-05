package io.github.sadellie.sukko.feature.editor

import android.content.Context
import android.content.Intent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.sadellie.sukko.core.common.collectAsStateWithLifecycleKMP
import io.github.sadellie.sukko.core.designsystem.PreviewScreenSizesContainer
import io.github.sadellie.sukko.core.medialistener.NotificationListener
import io.github.sadellie.sukko.core.model.WidgetData
import io.github.sadellie.sukko.core.model.WidgetSubscriptionInfo
import io.github.sadellie.sukko.core.model.basic.BrushSource
import io.github.sadellie.sukko.core.model.basic.M3Color
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.model.basic.ScriptableString
import io.github.sadellie.sukko.core.model.layer.ColdColumnLayer
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.sadellie.sukko.core.model.layer.EvaluatedTextLayer
import io.github.sadellie.sukko.core.model.modifier.ColdBackgroundColorModifier
import io.github.sadellie.sukko.core.model.modifier.ColdFillMaxSizeModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedBackgroundColorModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedFillMaxHeightModifier
import io.github.sadellie.sukko.core.model.modifier.EvaluatedFillMaxWidthModifier
import io.github.sadellie.sukko.core.ui.LoadingScaffoldWithTopAppBar
import io.github.sadellie.sukko.core.widget.MainWidgetProvider
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun EditorScene(
  onNavigateUp: () -> Unit,
  onNavigateToSaveAsPreset: () -> Unit,
  onNavigateToPresetSelector: () -> Unit,
  viewModel: EditorViewModel,
) {
  LaunchedEffect(Unit) { viewModel.onUpdateWidgetSize() }

  val uiState = viewModel.uiState.collectAsStateWithLifecycleKMP().value
  val context = LocalContext.current

  when (uiState) {
    null -> LoadingScaffoldWithTopAppBar(onNavigateUp = onNavigateUp, disableBack = false)
    else ->
      EditorScreen(
        uiState = uiState,
        onNavigateUp = onNavigateUp,
        onSave = { isChecksEnabled, preview ->
          val isNotificationListenerEnabled = NotificationListener.canAccessNotifications(context)
          viewModel.saveWidgetData(
            isChecksEnabled = isChecksEnabled,
            isNotificationListenerEnabled = isNotificationListenerEnabled,
            preview = preview,
          ) { widgetSubscriptionInfo ->
            updateWidget(context, uiState.widgetData.appWidgetId, widgetSubscriptionInfo)
          }
        },
        onRename = viewModel::renameWidget,
        onNavigateToSaveAsPreset = onNavigateToSaveAsPreset,
        onNavigateToLayer = viewModel::onNavigateToLayer,
        onEvent = viewModel::onEvent,
        onUpdateWidgetDataSaverState = viewModel::updateWidgetSaverState,
        onNavigateNotificationListener = {
          NotificationListener.openNotificationListenerPermission(context)
        },
        onNavigateToPresetSelector = onNavigateToPresetSelector,
      )
  }
}

private fun updateWidget(
  context: Context,
  appWidgetId: Int,
  widgetSubscriptionInfo: WidgetSubscriptionInfo,
) {
  val intent =
    Intent(context, MainWidgetProvider::class.java)
      .setAction(MainWidgetProvider.ACTION_UPDATE_WITH_SUBSCRIPTION)
      .putExtra(MainWidgetProvider.EXTRA_APPWIDGET_ID, appWidgetId)
      .putExtra(MainWidgetProvider.EXTRA_IS_TIME_SUBSCRIBER, widgetSubscriptionInfo.isTime)
      .putExtra(MainWidgetProvider.EXTRA_IS_BATTERY_SUBSCRIBER, widgetSubscriptionInfo.isBattery)
      .putExtra(MainWidgetProvider.EXTRA_IS_MEDIA_SUBSCRIBER, widgetSubscriptionInfo.isMedia)
  context.sendBroadcast(intent)
}

@PreviewScreenSizes
@Preview
@Composable
private fun PreviewEditorScreen(@PreviewParameter(PreviewCollection::class) dpSize: DpSize) =
  PreviewScreenSizesContainer {
    EditorScreen(
      uiState =
        EditorUIState(
          widgetData = WidgetData(appWidgetId = 1, name = "Widget 1"),
          widgetDataSaverState = WidgetDataSaverState.NotRunning,
          isWidgetDataSaved = false,
          evaluatedLayers =
            listOf(
              EvaluatedTextLayer(
                id = 0,
                parentId = null,
                name = "Text 2",
                widgetModifiers =
                  listOf(
                    EvaluatedBackgroundColorModifier(
                      id = 0,
                      color = SolidColor(MaterialTheme.colorScheme.background),
                      shape = RectangleShape,
                    ),
                    EvaluatedFillMaxWidthModifier(id = 1, fraction = 1f),
                    EvaluatedFillMaxHeightModifier(id = 2, fraction = 1f),
                  ),
                clickActions = emptyList(),
                textStyle = TextStyle(),
                text = "Basic text layer 22",
                textColor = SolidColor(MaterialTheme.colorScheme.onBackground),
              )
            ),
          canvasSize = dpSize,
          viewerState =
            ViewerState(
              currentLayer = ColdTextLayer(2, 1),
              parentLayer = null,
              breadcrumbs =
                listOf(ColdColumnLayer(0, null), ColdColumnLayer(1, 0), ColdTextLayer(2, 1)),
              loadedLayers =
                listOf(
                  ColdColumnLayer(
                    id = 0,
                    parentId = null,
                    widgetModifiers =
                      listOf(
                        ColdBackgroundColorModifier(
                          id = 0,
                          color =
                            BrushSource.SolidColor(ScriptableColor.FixedM3(M3Color.BACKGROUND)),
                        ),
                        ColdFillMaxSizeModifier(id = 1),
                      ),
                  ),
                  ColdTextLayer(id = 1, parentId = 0, text = ScriptableString.Fixed("text")),
                ),
            ),
        ),
      onNavigateUp = {},
      onSave = { _, _ -> },
      onNavigateToLayer = {},
      onEvent = {},
      onRename = {},
      onNavigateToSaveAsPreset = {},
      onUpdateWidgetDataSaverState = {},
      onNavigateNotificationListener = {},
      onNavigateToPresetSelector = {},
    )
  }

private class PreviewCollection(
  override val values: Sequence<DpSize> =
    sequenceOf(
      DpSize.Zero,
      DpSize(200.dp, 200.dp),
      DpSize(200.dp, 400.dp),
      DpSize(200.dp, 600.dp),
      DpSize(600.dp, 200.dp),
      DpSize(400.dp, 200.dp),
    )
) : PreviewParameterProvider<DpSize>

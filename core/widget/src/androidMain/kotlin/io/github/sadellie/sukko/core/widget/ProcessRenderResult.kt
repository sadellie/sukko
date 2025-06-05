package io.github.sadellie.sukko.core.widget

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.unit.DpSize
import androidx.core.graphics.scale
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.layer.Layer
import io.github.sadellie.sukko.core.unglance.RenderSubResult
import kotlin.math.roundToInt
import kotlinx.serialization.json.Json

internal suspend fun processRenderSubResult(
  context: Context,
  renderSubResult: RenderSubResult,
  evaluatedLayers: List<Layer.Evaluated>,
): RemoteViews {
  val imageBitmap = renderSubResult.graphicsLayer.toImageBitmap()
  val background = prepareRenderBackground(context, imageBitmap, renderSubResult.widgetSize)
  val hostLayout = RemoteViews(context.packageName, R.layout.render_host_main_layout)
  val clickableAreas =
    prepareClickableAreas(context, evaluatedLayers, imageBitmap, renderSubResult.bounds)
  hostLayout.removeAllViews(R.id.host_main_view)
  hostLayout.addView(R.id.host_main_view, background)
  hostLayout.addView(R.id.host_main_view, clickableAreas)
  return hostLayout
}

private fun prepareRenderBackground(
  context: Context,
  image: ImageBitmap,
  widgetSize: DpSize,
): RemoteViews {
  val renderHostImage = RemoteViews(context.packageName, R.layout.render_host_image_layout)
  renderHostImage.setViewLayoutWidth(
    R.id.host_image_view,
    image.width.toFloat(),
    TypedValue.COMPLEX_UNIT_PX,
  )
  renderHostImage.setViewLayoutHeight(
    R.id.host_image_view,
    image.height.toFloat(),
    TypedValue.COMPLEX_UNIT_PX,
  )

  // image is rendered with higher resolution
  val androidBitmap = image.asAndroidBitmap()
  val density = context.resources.displayMetrics.density
  val scaledWidgetSize = widgetSize * density
  val scaleImageBitmap =
    androidBitmap.scale(
      width = scaledWidgetSize.width.value.roundToInt(),
      height = scaledWidgetSize.height.value.roundToInt(),
    )
  renderHostImage.setImageViewBitmap(R.id.host_image_view, scaleImageBitmap)

  return renderHostImage
}

private fun prepareClickableAreas(
  context: Context,
  evaluatedLayers: List<Layer.Evaluated>,
  image: ImageBitmap,
  bounds: Map<Int, Rect>,
): RemoteViews {
  val clickableHost = RemoteViews(context.packageName, R.layout.render_host_clickable_layout)
  clickableHost.setViewLayoutWidth(
    R.id.host_clickable_view,
    image.width.toFloat(),
    TypedValue.COMPLEX_UNIT_PX,
  )
  clickableHost.setViewLayoutHeight(
    R.id.host_clickable_view,
    image.height.toFloat(),
    TypedValue.COMPLEX_UNIT_PX,
  )

  for (layer in evaluatedLayers) {
    val clickActions = layer.clickActions
    if (clickActions.isEmpty()) continue
    val layerBounds = bounds[layer.id]
    if (layerBounds == null) {
      Logger.e(TAG) { "Failed to get layer coordinates" }
      continue
    }
    val clickableRemoteViews =
      RemoteViews(context.packageName, R.layout.render_host_clickable_area_layout)
    // size
    clickableRemoteViews.setViewLayoutWidth(
      R.id.host_clickable_area_view,
      layerBounds.width,
      TypedValue.COMPLEX_UNIT_PX,
    )
    clickableRemoteViews.setViewLayoutHeight(
      R.id.host_clickable_area_view,
      layerBounds.height,
      TypedValue.COMPLEX_UNIT_PX,
    )
    // coordinates
    clickableRemoteViews.setViewLayoutMargin(
      R.id.host_clickable_area_view,
      RemoteViews.MARGIN_START,
      layerBounds.left,
      TypedValue.COMPLEX_UNIT_PX,
    )
    clickableRemoteViews.setViewLayoutMargin(
      R.id.host_clickable_area_view,
      RemoteViews.MARGIN_TOP,
      layerBounds.top,
      TypedValue.COMPLEX_UNIT_PX,
    )

    val intentOnClick = createPendingIntentFromClickAction(context, layer.id, clickActions)
    clickableRemoteViews.setOnClickPendingIntent(R.id.host_clickable_area_view, intentOnClick)

    clickableHost.addView(R.id.host_clickable_view, clickableRemoteViews)
  }

  return clickableHost
}

private fun createPendingIntentFromClickAction(
  context: Context,
  layerId: Int,
  clickActions: List<ClickAction.Evaluated>,
): PendingIntent {
  val clickActionsArray = clickActions.map { Json.encodeToString(it) }.toTypedArray()
  val intent =
    Intent(context, MainWidgetProvider::class.java)
      .setAction(MainWidgetProvider.ACTION_CLICK)
      .putExtra(MainWidgetProvider.EXTRA_ACTION_CLICKS_ARRAY, clickActionsArray)

  return PendingIntent.getBroadcast(
    context,
    layerId,
    intent,
    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
  )
}

private const val TAG = "UnglanceCompose"

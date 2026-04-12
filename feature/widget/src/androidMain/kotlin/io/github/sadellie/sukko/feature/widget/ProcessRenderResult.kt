package io.github.sadellie.sukko.feature.widget

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.DisplayMetrics
import android.util.SizeF
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
import io.github.sadellie.sukko.core.unglance.RenderResult
import io.github.sadellie.sukko.core.unglance.RenderSubResult
import kotlinx.serialization.json.Json

/**
 * Turns [renderResult] into a [RemoteViews]. This method rescales render results to fit into memory
 * usage limit (1.5 times of screen size). This limit is applied to entire [RemoteViews], not just
 * one item so scaling is applied to all results.
 */
internal suspend fun processAllRenderSubResults(
  context: Context,
  renderResult: RenderResult.Ready,
): RemoteViews {
  val displayMetrics: DisplayMetrics = context.resources.displayMetrics
  val screenWidth = displayMetrics.widthPixels
  val screenHeight = displayMetrics.heightPixels
  // 4 is for ARGB_8888 (4 bytes per pixel)
  // to make this a bit safer limit is calculated with 1.4 instead of 1.5
  val maxMemoryInBytes = screenWidth * screenHeight * 4 * 1.4f

  var totalMemory = 0f
  for (subResult in renderResult.subResults) {
    val imageBitmap = subResult.graphicsLayer.toImageBitmap()
    val subResultMemoryUsage = imageBitmap.width * imageBitmap.height * 4
    totalMemory += subResultMemoryUsage
  }

  // do not upscale
  val scaleFactor = (maxMemoryInBytes / totalMemory).coerceAtMost(1f)
  val remoteViews =
    RemoteViews(
      renderResult.subResults.associate { subResult ->
        subResult.widgetSize.toSizeF() to
          processRenderSubResult(context, subResult, renderResult.layers, scaleFactor)
      }
    )

  return remoteViews
}

internal suspend fun processRenderSubResult(
  context: Context,
  renderSubResult: RenderSubResult,
  evaluatedLayers: List<Layer.Evaluated>,
  bitmapScaleFactor: Float,
): RemoteViews {
  val imageBitmap = renderSubResult.graphicsLayer.toImageBitmap()
  val background =
    imageBitmap
      .asAndroidBitmap()
      .scale(
        (imageBitmap.width * bitmapScaleFactor).toInt(),
        (imageBitmap.height * bitmapScaleFactor).toInt(),
      )
  val hostLayout = RemoteViews(context.packageName, R.layout.render_host_main_layout)
  hostLayout.setViewLayoutWidth(
    R.id.host_main_view,
    imageBitmap.width.toFloat(),
    TypedValue.COMPLEX_UNIT_PX,
  )
  hostLayout.setViewLayoutHeight(
    R.id.host_main_view,
    imageBitmap.height.toFloat(),
    TypedValue.COMPLEX_UNIT_PX,
  )
  hostLayout.setImageViewBitmap(R.id.host_main_view, background)
  val clickableAreas =
    prepareClickableAreas(context, evaluatedLayers, imageBitmap, renderSubResult.bounds)
  hostLayout.removeAllViews(R.id.host_main_view)
  hostLayout.addView(R.id.host_main_view, clickableAreas)
  return hostLayout
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
      Logger.e(tag = TAG) { "Failed to get layer coordinates" }
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

private fun DpSize.toSizeF(): SizeF = SizeF(width.value, height.value)

private const val TAG = "UnglanceCompose"

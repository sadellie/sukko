package io.github.sadellie.sukko.core.common

import android.appwidget.AppWidgetManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.SizeF
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import kotlin.math.roundToInt

fun AppWidgetManager.appWidgetSize(appWidgetId: Int, orientation: Int): DpSize {
  val options = this.getAppWidgetOptions(appWidgetId)
  val widgetSize =
    if (orientation == Configuration.ORIENTATION_PORTRAIT) options.portraitWidgetSize()
    else options.landscapeWidgetSize()
  return widgetSize
}

fun AppWidgetManager.appWidgetSizes(appWidgetId: Int): List<DpSize> {
  val options = this.getAppWidgetOptions(appWidgetId)
  @Suppress("Deprecation")
  val sizes = options.getParcelableArrayList<SizeF>(AppWidgetManager.OPTION_APPWIDGET_SIZES)
  return if (sizes.isNullOrEmpty()) {
      val estimatedSizes = listOf(options.portraitWidgetSize(), options.landscapeWidgetSize())
      Logger.w("AppWidgetUtils") { "appWidgetSizes: Fallback to estimation: $estimatedSizes" }
      estimatedSizes
    } else {
      // pixel launcher report really similar sizes
      sizes.map { DpSize(it.width.roundToInt().dp, it.height.roundToInt().dp) }
    }
    .distinct()
}

private fun Bundle.portraitWidgetSize(): DpSize {
  val width = this.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0)
  val height = this.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 0)
  // size doesn't matter if one of the sides is zero
  if (width == 0 || height == 0) return DpSize(0.dp, 0.dp)
  return DpSize(width.dp, height.dp)
}

private fun Bundle.landscapeWidgetSize(): DpSize {
  val width = this.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, 0)
  val height = this.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)
  // size doesn't matter if one of the sides is zero
  if (width == 0 || height == 0) return DpSize(0.dp, 0.dp)
  return DpSize(width.dp, height.dp)
}

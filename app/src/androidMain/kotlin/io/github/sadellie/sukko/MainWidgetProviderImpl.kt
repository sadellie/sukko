package io.github.sadellie.sukko

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import coil3.ImageLoader
import io.github.sadellie.sukko.core.data.LayerEvaluator
import io.github.sadellie.sukko.core.data.ScriptableEvaluator
import io.github.sadellie.sukko.core.data.WidgetDataRepository
import io.github.sadellie.sukko.core.data.WidgetSubscriptionsRepository
import io.github.sadellie.sukko.core.medialistener.MediaListener
import io.github.sadellie.sukko.feature.widget.MainWidgetProvider

class MainWidgetProviderImpl : MainWidgetProvider() {
  override fun getWidgetDataRepository(context: Context): WidgetDataRepository =
    getApplicationGraph(context.applicationContext).widgetDataRepository

  override fun getMediaListener(context: Context): MediaListener =
    getApplicationGraph(context.applicationContext).mediaListener

  override fun getImageLoader(context: Context): ImageLoader =
    getApplicationGraph(context.applicationContext).imageLoader

  override fun getAllWidgetIds(context: Context): List<Int> {
    val componentName =
      ComponentName(context.applicationContext, MainWidgetProviderImpl::class.java)
    val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)
    val allWidgetIds = appWidgetManager.getAppWidgetIds(componentName).toList()
    return allWidgetIds
  }

  override fun getWidgetProviderIntent(context: Context): Intent =
    Intent(context.applicationContext, MainWidgetProviderImpl::class.java)

  override fun getWidgetSubscriptionsRepository(context: Context): WidgetSubscriptionsRepository =
    getApplicationGraph(context.applicationContext).widgetSubscriptionsRepository

  override fun startMediaListenerService(context: Context) =
    MediaListenerService.start(context.applicationContext)

  override fun stopMediaListenerService(context: Context) =
    MediaListenerService.stop(context.applicationContext)

  override fun getLayerEvaluatorFactory(context: Context): LayerEvaluator.LayerEvaluatorFactory =
    getApplicationGraph(context.applicationContext).layerEvaluatorFactory

  override fun getScriptableEvaluatorFactory(
    context: Context
  ): ScriptableEvaluator.ScriptableEvaluatorFactory =
    getApplicationGraph(context.applicationContext).scriptableEvaluatorFactory
}

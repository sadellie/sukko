package io.github.sadellie.sukko

import android.app.Application
import android.content.Context
import android.content.Intent
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import coil3.ImageLoader
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.createGraphFactory
import io.github.sadellie.sukko.core.common.filesPath
import io.github.sadellie.sukko.core.data.DataBindings
import io.github.sadellie.sukko.core.data.ImageProvider
import io.github.sadellie.sukko.core.data.LayerEvaluator
import io.github.sadellie.sukko.core.data.ScriptableEvaluator
import io.github.sadellie.sukko.core.data.WidgetDataRepository
import io.github.sadellie.sukko.core.data.WidgetSubscriptionsRepository
import io.github.sadellie.sukko.core.database.DatabaseBindings
import io.github.sadellie.sukko.core.medialistener.MediaListener
import io.github.sadellie.sukko.core.medialistener.MediaListenerImpl
import okio.Path

class SukkoApplication : Application() {
  val appGraph by lazy {
    createGraphFactory<AndroidAppGraph.Factory>()
      .create(
        application = this,
        databaseBindings = DatabaseBindings(),
        dataBindings = DataBindings(),
      )
  }

  override fun onCreate() {
    super.onCreate()
    // todo val logSeverity = if (BuildConfig.DEBUG) Severity.Verbose else Severity.Warn
    val logSeverity = Severity.Verbose
    Logger.setMinSeverity(logSeverity)
  }
}

@DependencyGraph(AppScope::class)
interface AndroidAppGraph : AppGraph {
  @DependencyGraph.Factory
  fun interface Factory {
    fun create(
      @Provides application: Application,
      @Includes databaseBindings: DatabaseBindings,
      @Includes dataBindings: DataBindings,
    ): AndroidAppGraph
  }

  val mediaListener: MediaListener
  val imageLoader: ImageLoader
  val widgetDataRepository: WidgetDataRepository
  val imageProvider: ImageProvider
  val layerEvaluatorFactory: LayerEvaluator.LayerEvaluatorFactory
  val scriptableEvaluatorFactory: ScriptableEvaluator.ScriptableEvaluatorFactory
  val widgetSubscriptionsRepository: WidgetSubscriptionsRepository

  @Named("filesDirPath")
  @Provides
  private fun provideFilesDirPath(application: Application): Path = application.filesPath

  @Provides
  private fun provideContext(application: Application): Context = application.applicationContext

  @SingleIn(AppScope::class)
  @Provides
  private fun provideMediaListener(context: Context, imageProvider: ImageProvider): MediaListener =
    MediaListenerImpl(
      context = context,
      onMetadataUpdate = { context, action ->
        val intent = Intent(context, MainWidgetProviderImpl::class.java).setAction(action)
        context.sendBroadcast(intent)
      },
      imageProvider = imageProvider,
    )
}

internal fun getApplicationGraph(context: Context): AndroidAppGraph =
  (context as? SukkoApplication)?.appGraph ?: error("wrong $context type")

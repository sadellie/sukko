package io.github.sadellie.sukko

import android.app.Application
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import io.github.sadellie.sukko.core.data.dataModule
import io.github.sadellie.sukko.core.importexport.importExportModule
import io.github.sadellie.sukko.core.medialistener.MediaListener
import io.github.sadellie.sukko.core.medialistener.MediaListenerImpl
import io.github.sadellie.sukko.feature.editor.editorModule
import io.github.sadellie.sukko.feature.fontseditor.fontsEditorModule
import io.github.sadellie.sukko.feature.home.homeModule
import io.github.sadellie.sukko.feature.iconpackeditor.iconPackEditorModule
import io.github.sadellie.sukko.feature.importpreset.importPresetModule
import io.github.sadellie.sukko.feature.presetselector.presetSelectorModule
import io.github.sadellie.sukko.feature.saveaspreset.saveAsPresetModule
import io.github.sadellie.sukko.feature.settings.settingsModule
import io.github.sadellie.sukko.feature.widget.MainWidgetProvider
import io.github.sadellie.sukko.feature.widgetinfo.widgetInfoModule
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.lazyModules
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.koinConfiguration
import org.koin.dsl.lazyModule

@OptIn(KoinExperimentalAPI::class)
class SukkoApplication : Application(), KoinStartup {
  override fun onCreate() {
    super.onCreate()
    // todo val logSeverity = if (BuildConfig.DEBUG) Severity.Verbose else Severity.Warn
    val logSeverity = Severity.Verbose
    Logger.setMinSeverity(logSeverity)
  }

  override fun onKoinStartup(): KoinConfiguration = koinConfiguration {
    androidContext(this@SukkoApplication)
    lazyModules(
      dataModule,
      homeModule,
      editorModule,
      widgetInfoModule,
      saveAsPresetModule,
      iconPackEditorModule,
      importPresetModule,
      fontsEditorModule,
      importPresetModule,
      importExportModule,
      settingsModule,
      presetSelectorModule,
      listeners,
    )
  }
}

private val listeners = lazyModule {
  single<MediaListener> {
    MediaListenerImpl(
      context = androidContext(),
      onMetadataUpdate = { context, action -> MainWidgetProvider.sendBroadcast(context, action) },
      imageProvider = get(),
    )
  }
}

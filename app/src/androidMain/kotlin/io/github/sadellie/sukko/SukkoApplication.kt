package io.github.sadellie.sukko

import android.app.Application
import androidx.room.Room
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import io.github.sadellie.sukko.core.common.defaultIODispatcher
import io.github.sadellie.sukko.core.database.SukkoDatabase
import io.github.sadellie.sukko.core.fontfiles.FontFamilyLoader
import io.github.sadellie.sukko.core.fontfiles.FontFile
import io.github.sadellie.sukko.core.fontfiles.FontFileCustomRepository
import io.github.sadellie.sukko.core.fontfiles.FontFileCustomRepositoryImpl
import io.github.sadellie.sukko.core.iconfiles.IconFile
import io.github.sadellie.sukko.core.importexport.WidgetDataPresetExportImport
import io.github.sadellie.sukko.core.medialistener.MediaListener
import io.github.sadellie.sukko.core.medialistener.MediaListenerImpl
import io.github.sadellie.sukko.core.remote.RemoteClient
import io.github.sadellie.sukko.core.script.docs.DocsRepository
import io.github.sadellie.sukko.core.script.docs.DocsRepositoryImpl
import io.github.sadellie.sukko.core.widget.MainWidgetProvider
import io.github.sadellie.sukko.feature.editor.EditorViewModel
import io.github.sadellie.sukko.feature.editor.selector.AppSelectorViewModel
import io.github.sadellie.sukko.feature.editor.selector.FontFileSelectorViewModel
import io.github.sadellie.sukko.feature.editor.selector.IconSelectorViewModel
import io.github.sadellie.sukko.feature.editor.selector.scripteditor.DocsViewModel
import io.github.sadellie.sukko.feature.fontseditor.FontsEditorViewModel
import io.github.sadellie.sukko.feature.home.PresetsViewModel
import io.github.sadellie.sukko.feature.home.WidgetsViewModel
import io.github.sadellie.sukko.feature.icopackeditor.IconPackEditorViewModel
import io.github.sadellie.sukko.feature.icopackeditor.IconPacksViewModel
import io.github.sadellie.sukko.feature.importpreset.ImportPresetViewModel
import io.github.sadellie.sukko.feature.presetselector.PresetSelectorViewModel
import io.github.sadellie.sukko.feature.saveaspreset.SaveAsPresetViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.lazyModules
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.koinConfiguration
import org.koin.dsl.lazyModule

@OptIn(KoinExperimentalAPI::class)
class SukkoApplication : Application(), KoinStartup {
  override fun onCreate() {
    super.onCreate()
    val logSeverity = if (BuildConfig.DEBUG) Severity.Verbose else Severity.Warn
    Logger.setMinSeverity(logSeverity)
  }

  override fun onKoinStartup(): KoinConfiguration = koinConfiguration {
    androidContext(this@SukkoApplication)
    lazyModules(userData, data, viewModels, listeners, loaders)
  }
}

private val userData = lazyModule {
  single {
    Room.databaseBuilder<SukkoDatabase>(androidContext(), SukkoDatabase.DATABASE_NAME)
      .fallbackToDestructiveMigration(false)
      .fallbackToDestructiveMigrationOnDowngrade(true)
      .setQueryCoroutineContext(defaultIODispatcher)
      .build()
  }
}

private val data = lazyModule {
  this.allDataModules()
  factoryOf(::WidgetDataPresetExportImport)
  factory<FontFileCustomRepository> { FontFileCustomRepositoryImpl(context = androidContext()) }
  single<RemoteClient> { RemoteClient() }
  factory<DocsRepository> { DocsRepositoryImpl() }
}

private val viewModels = lazyModule {
  viewModelOf(::WidgetsViewModel)
  viewModelOf(::PresetsViewModel)
  viewModelOf(::EditorViewModel)
  viewModelOf(::SaveAsPresetViewModel)
  viewModelOf(::IconSelectorViewModel)
  viewModel<FontFileSelectorViewModel> { (initialValue: FontFile?) ->
    FontFileSelectorViewModel(initialValue = initialValue, fontFileCustomRepository = get())
  }
  viewModelOf(::IconPacksViewModel)
  viewModelOf(::IconPackEditorViewModel)
  viewModelOf(::ImportPresetViewModel)
  viewModelOf(::FontsEditorViewModel)
  viewModelOf(::PresetSelectorViewModel)
  viewModelOf(::DocsViewModel)
  viewModel<AppSelectorViewModel> { (packageName: String?) ->
    AppSelectorViewModel(packageName = packageName, installedAppsProvider = get())
  }
}

private val listeners = lazyModule {
  single<MediaListener> {
    MediaListenerImpl(
      context = androidContext(),
      onMetadataUpdate = { context, action -> MainWidgetProvider.sendBroadcast(context, action) },
    )
  }
}

@OptIn(KoinExperimentalAPI::class, ExperimentalCoilApi::class)
val loaders = lazyModule {
  single<ImageLoader> { ImageLoader.Builder(androidContext()).build() }
  single<FontFamilyLoader> { FontFamilyLoader() }
}

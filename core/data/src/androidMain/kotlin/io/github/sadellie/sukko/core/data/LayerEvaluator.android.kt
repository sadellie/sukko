package io.github.sadellie.sukko.core.data

import android.content.Context
import io.github.sadellie.sukko.core.medialistener.MediaListener
import io.github.sadellie.sukko.core.model.GlobalValueCache

fun LayerEvaluator.invalidateOnAlarmProviders(androidContext: Context): LayerEvaluator {
  val newGlobalValueCache = GlobalValueCache()
  return LayerEvaluator(
    layers = layers,
    imageProvider = imageProvider,
    scriptableEvaluator =
      ScriptableEvaluator(
        globals = scriptableEvaluator.globals,
        globalValueCache = newGlobalValueCache,
        scriptableEvaluatorContext =
          scriptableEvaluator.scriptableEvaluatorContext.copy(
            dateTimeProvider = DateTimeProviderImpl(),
            batteryInfoProvider = BatteryInfoProviderImpl(androidContext),
          ),
        globalCurrentValueStore = scriptableEvaluator.globalCurrentValueStore?.clearCache(),
      ),
    filesDirPath = filesDirPath,
    fontFamilyLoader = fontFamilyLoader,
    textStyleSourceEvaluator = textStyleSourceEvaluator,
  )
}

fun LayerEvaluator.invalidateMediaProvider(
  androidContext: Context,
  mediaListener: MediaListener,
): LayerEvaluator {
  val newGlobalValueCache = GlobalValueCache()
  return LayerEvaluator(
    layers = layers,
    imageProvider = imageProvider,
    scriptableEvaluator =
      ScriptableEvaluator(
        globals = scriptableEvaluator.globals,
        globalValueCache = newGlobalValueCache,
        scriptableEvaluatorContext =
          scriptableEvaluator.scriptableEvaluatorContext.copy(
            mediaInfoProvider = MediaInfoProviderImpl(androidContext, mediaListener)
          ),
        globalCurrentValueStore = scriptableEvaluator.globalCurrentValueStore?.clearCache(),
      ),
    filesDirPath = filesDirPath,
    fontFamilyLoader = fontFamilyLoader,
    textStyleSourceEvaluator = textStyleSourceEvaluator,
  )
}

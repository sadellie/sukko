package io.github.sadellie.sukko.core.data

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import co.touchlab.kermit.Logger
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named
import io.github.sadellie.sukko.core.fontfiles.FontFamilyLoader
import io.github.sadellie.sukko.core.model.GlobalValueCache
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.basic.TextStyleSource
import io.github.sadellie.sukko.core.model.basic.TextStyleSource.Companion.fontSizeRange
import io.github.sadellie.sukko.core.model.basic.TextStyleSource.Companion.fontWeightRange
import okio.Path

class TextStyleSourceEvaluator(
  private val filesDirPath: Path,
  private val fontFamilyLoader: FontFamilyLoader,
  private val globalValueCache: GlobalValueCache,
  private val scriptableEvaluator: ScriptableEvaluator,
  private val globals: Globals,
) {
  @Inject
  class Factory(
    @param:Named("filesDirPath") private val filesDirPath: Path,
    private val fontFamilyLoader: FontFamilyLoader,
    private val scriptableEvaluatorFactory: ScriptableEvaluator.ScriptableEvaluatorFactory,
  ) {
    fun create(globals: Globals, globalValueCache: GlobalValueCache): TextStyleSourceEvaluator =
      TextStyleSourceEvaluator(
        filesDirPath = filesDirPath,
        fontFamilyLoader = fontFamilyLoader,
        globalValueCache = globalValueCache,
        scriptableEvaluator = scriptableEvaluatorFactory.create(globals),
        globals = globals,
      )

    fun create(
      globals: Globals,
      globalValueCache: GlobalValueCache,
      scriptableEvaluator: ScriptableEvaluator,
    ): TextStyleSourceEvaluator =
      TextStyleSourceEvaluator(
        filesDirPath = filesDirPath,
        fontFamilyLoader = fontFamilyLoader,
        globalValueCache = globalValueCache,
        scriptableEvaluator = scriptableEvaluator,
        globals = globals,
      )
  }

  suspend fun evaluate(textStyleSource: TextStyleSource): TextStyle {
    return when (textStyleSource) {
      is TextStyleSource.Global -> {
        val globalTextStyle = globals.findTextStyle(textStyleSource.id) ?: return TextStyle()
        globalValueCache.getOrPut(globalTextStyle) {
          Logger.d(tag = TAG) { "evaluate global: ${globalTextStyle.id} ${globalTextStyle.label}" }
          evaluate(globalTextStyle.initialValue)
        }
      }

      is TextStyleSource.Local ->
        TextStyle(
          fontSize =
            scriptableEvaluator.evaluateDouble(textStyleSource.fontSize).coerceIn(fontSizeRange).sp,
          textAlign = textStyleSource.textAlignSource.getTextAlign(),
          fontWeight =
            FontWeight(
              scriptableEvaluator
                .evaluateDouble(textStyleSource.fontWeight)
                .coerceIn(fontWeightRange)
                .toInt()
            ),
          fontFamily = fontFamilyLoader.loadFromFontFile(textStyleSource.fontFile, filesDirPath),
          fontStyle = textStyleSource.fontStyle.getFontStyle(),
        )
    }
  }
}

private const val TAG = "TextStyleSourceEvaluator"

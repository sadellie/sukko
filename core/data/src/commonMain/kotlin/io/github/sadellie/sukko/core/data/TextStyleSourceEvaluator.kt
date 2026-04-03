package io.github.sadellie.sukko.core.data

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.LayerContext
import io.github.sadellie.sukko.core.model.basic.TextStyleSource
import io.github.sadellie.sukko.core.model.basic.TextStyleSource.Companion.fontSizeRange
import io.github.sadellie.sukko.core.model.basic.TextStyleSource.Companion.fontWeightRange

class TextStyleSourceEvaluator(
  private val textStyleSource: TextStyleSource,
  private val layerContext: LayerContext,
  private val globals: Globals,
) {
  suspend fun evaluate(): TextStyle {
    return when (textStyleSource) {
      is TextStyleSource.Global -> {
        val globalTextStyle = globals.findTextStyle(textStyleSource.id) ?: return TextStyle()
        layerContext.globalValueCache.getOrPut(globalTextStyle) {
          Logger.d(tag = TAG) { "evaluate global: ${globalTextStyle.id} ${globalTextStyle.label}" }
          TextStyleSourceEvaluator(globalTextStyle.value, layerContext, globals).evaluate()
        } ?: TextStyle()
      }

      is TextStyleSource.Local ->
        TextStyle(
          fontSize =
            textStyleSource.fontSize
              .getValue(layerContext, globals)
              .value
              .coerceIn(fontSizeRange)
              .sp,
          textAlign = textStyleSource.textAlignSource.getTextAlign(),
          fontWeight =
            FontWeight(
              textStyleSource.fontWeight
                .getValue(layerContext, globals)
                .coerceIn(fontWeightRange)
                .toInt()
            ),
          fontFamily = layerContext.loadFontFamily(textStyleSource.fontFile),
          fontStyle = textStyleSource.fontStyle.getFontStyle(),
        )
    }
  }
}

private const val TAG = "TextStyleSourceEvaluator"

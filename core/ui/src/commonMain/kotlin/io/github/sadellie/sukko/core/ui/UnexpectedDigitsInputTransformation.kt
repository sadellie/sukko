package io.github.sadellie.sukko.core.ui

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.delete
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
class InputTransformationDouble(
  private val valueRange: ClosedRange<Double> = -Double.MAX_VALUE..Double.MAX_VALUE,
  override val allowFraction: Boolean,
) : UnexpectedDigitsInputTransformation<Double> {
  override val keyboardOptions: KeyboardOptions
    get() =
      KeyboardOptions(
        autoCorrectEnabled = false,
        keyboardType = if (allowFraction) KeyboardType.Decimal else KeyboardType.Number,
      )

  override val allowNegative = valueRange.start < 0

  override fun toValue(text: CharSequence): Double? = text.toString().toDoubleOrNull()

  override fun TextFieldBuffer.onFiltered(value: Double?) {
    when {
      // not valid, do nothing
      value == null -> Unit
      value < valueRange.start -> replace(0, length, valueRange.start.toString())
      value > valueRange.endInclusive -> replace(0, length, valueRange.endInclusive.toString())
    }
  }
}

@Stable
class InputTransformationFloat(
  private val valueRange: ClosedRange<Float> = -Float.MAX_VALUE..Float.MAX_VALUE,
  override val allowFraction: Boolean = true,
) : UnexpectedDigitsInputTransformation<Float> {
  override val keyboardOptions: KeyboardOptions
    get() =
      KeyboardOptions(
        autoCorrectEnabled = false,
        keyboardType = if (allowFraction) KeyboardType.Decimal else KeyboardType.Number,
      )

  override val allowNegative = valueRange.start < 0

  override fun toValue(text: CharSequence): Float? = text.toString().toFloatOrNull()

  override fun TextFieldBuffer.onFiltered(value: Float?) {
    when {
      // not valid, do nothing
      value == null -> Unit
      value < valueRange.start -> replace(0, length, valueRange.start.toString())
      value > valueRange.endInclusive -> replace(0, length, valueRange.endInclusive.toString())
    }
  }
}

@Stable
class InputTransformationDp(
  private val valueRange: ClosedRange<Dp> = -Dp.Infinity..Dp.Infinity,
  override val allowFraction: Boolean = true,
) : UnexpectedDigitsInputTransformation<Dp> {
  override val allowNegative = valueRange.start.value < 0f

  override fun toValue(text: CharSequence) = text.toString().toFloatOrNull()?.dp

  override fun TextFieldBuffer.onFiltered(value: Dp?) {
    when {
      // not valid, do nothing
      value == null -> Unit
      value.value < valueRange.start.value -> replace(0, length, valueRange.start.value.toString())
      value.value > valueRange.endInclusive.value ->
        replace(0, length, valueRange.endInclusive.value.toString())
    }
  }
}

/**
 * - Allow any digit
 * - Allow using any fractional symbol as input (. and ,), but only if [allowFraction] is `true`
 * - Allow using minus symbols if [allowNegative] is `true`
 */
private interface UnexpectedDigitsInputTransformation<T> : InputTransformation {
  val allowFraction: Boolean
  val allowNegative: Boolean

  override fun TextFieldBuffer.transformInput() {
    this.filterLegal()
    this.onFiltered(toValue(this.toString()))
  }

  fun TextFieldBuffer.onFiltered(value: T?)

  fun toValue(text: CharSequence): T?

  private fun TextFieldBuffer.filterLegal() {
    if (length == 0) return
    val legalTokens = mutableSetOf('1', '2', '3', '4', '5', '6', '7', '8', '9', '0')
    if (allowNegative) {
      legalTokens.add('-')
    }

    if (allowFraction) {
      legalTokens.add('.')
      legalTokens.add(',')
    }

    var cursor = 0
    while (cursor < length) {
      val char = charAt(cursor)
      val isTokenLegal = char in legalTokens
      if (char == ',' || char == '.') {
        // found fractional, replace to dot and make fractional illegal for other iterations
        replace(cursor, cursor + 1, ".")
        legalTokens.remove('.')
        legalTokens.remove(',')
      }

      if (isTokenLegal) cursor++ else delete(cursor, cursor + 1)
    }
  }
}

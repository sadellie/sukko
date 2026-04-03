package io.github.sadellie.sukko.core.model.basic

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.Serializable

sealed interface GlobalValue<T> {
  val id: Long
  val label: String
  val value: T

  fun updateLabel(newLabel: String): GlobalValue<T>

  fun updateValue(newValue: T): GlobalValue<T>

  @Serializable
  data class GlobalString(
    override val id: Long = -1,
    override val label: String,
    override val value: ScriptableString = ScriptableString.Fixed("text"),
  ) : GlobalValue<ScriptableString> {

    override fun updateLabel(newLabel: String) = this.copy(label = newLabel)

    override fun updateValue(newValue: ScriptableString) = this.copy(value = newValue)
  }

  @Serializable
  data class GlobalBoolean(
    override val id: Long = -1,
    override val label: String,
    override val value: ScriptableBoolean = ScriptableBoolean.Fixed(false),
  ) : GlobalValue<ScriptableBoolean> {
    override fun updateLabel(newLabel: String) = this.copy(label = newLabel)

    override fun updateValue(newValue: ScriptableBoolean) = this.copy(value = newValue)
  }

  @Serializable
  data class GlobalDouble(
    override val id: Long = -1,
    override val label: String,
    override val value: ScriptableDouble = ScriptableDouble.Fixed(0.0),
  ) : GlobalValue<ScriptableDouble> {
    override fun updateLabel(newLabel: String) = this.copy(label = newLabel)

    override fun updateValue(newValue: ScriptableDouble) = this.copy(value = newValue)
  }

  @Serializable
  data class GlobalDp(
    override val id: Long = -1,
    override val label: String,
    override val value: ScriptableDp = ScriptableDp.Fixed(0.dp),
  ) : GlobalValue<ScriptableDp> {
    override fun updateLabel(newLabel: String) = this.copy(label = newLabel)

    override fun updateValue(newValue: ScriptableDp) = this.copy(value = newValue)
  }

  @Serializable
  data class GlobalSp(
    override val id: Long = -1,
    override val label: String,
    override val value: ScriptableSp = ScriptableSp.Fixed(0.sp),
  ) : GlobalValue<ScriptableSp> {
    override fun updateLabel(newLabel: String) = this.copy(label = newLabel)

    override fun updateValue(newValue: ScriptableSp) = this.copy(value = newValue)
  }

  @Serializable
  data class GlobalColor(
    override val id: Long = -1,
    override val label: String,
    override val value: ScriptableColor = ScriptableColor.FixedM3(M3Color.PRIMARY),
  ) : GlobalValue<ScriptableColor> {
    override fun updateLabel(newLabel: String) = this.copy(label = newLabel)

    override fun updateValue(newValue: ScriptableColor) = this.copy(value = newValue)
  }

  @Serializable
  data class GlobalTextStyle(
    override val id: Long = -1,
    override val label: String,
    override val value: TextStyleSource = TextStyleSource.Local(),
  ) : GlobalValue<TextStyleSource> {
    override fun updateLabel(newLabel: String) = this.copy(label = newLabel)

    override fun updateValue(newValue: TextStyleSource) = this.copy(value = newValue)
  }
}

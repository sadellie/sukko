package io.github.sadellie.sukko.core.model.basic

import kotlinx.serialization.Serializable

sealed interface GlobalValue<T> {
  val id: Long
  val label: String
  val initialValue: T

  fun updateLabel(newLabel: String): GlobalValue<T>

  fun updateInitialValue(value: T): GlobalValue<T>

  @Serializable
  data class GlobalString(
    override val id: Long = -1,
    override val label: String,
    override val initialValue: ScriptableString = ScriptableString.Fixed("text"),
  ) : GlobalValue<ScriptableString> {
    override fun updateLabel(newLabel: String) = this.copy(label = newLabel)

    override fun updateInitialValue(value: ScriptableString) = this.copy(initialValue = value)
  }

  @Serializable
  data class GlobalBoolean(
    override val id: Long = -1,
    override val label: String,
    override val initialValue: ScriptableBoolean = ScriptableBoolean.Fixed(false),
  ) : GlobalValue<ScriptableBoolean> {
    override fun updateLabel(newLabel: String) = this.copy(label = newLabel)

    override fun updateInitialValue(value: ScriptableBoolean) = this.copy(initialValue = value)
  }

  @Serializable
  data class GlobalDouble(
    override val id: Long = -1,
    override val label: String,
    override val initialValue: ScriptableDouble = ScriptableDouble.Fixed(0.0),
  ) : GlobalValue<ScriptableDouble> {
    override fun updateLabel(newLabel: String) = this.copy(label = newLabel)

    override fun updateInitialValue(value: ScriptableDouble) = this.copy(initialValue = value)
  }

  @Serializable
  data class GlobalColor(
    override val id: Long = -1,
    override val label: String,
    override val initialValue: ScriptableColor = ScriptableColor.FixedM3(M3Color.PRIMARY),
  ) : GlobalValue<ScriptableColor> {
    override fun updateLabel(newLabel: String) = this.copy(label = newLabel)

    override fun updateInitialValue(value: ScriptableColor) = this.copy(initialValue = value)
  }

  @Serializable
  data class GlobalTextStyle(
    override val id: Long = -1,
    override val label: String,
    override val initialValue: TextStyleSource = TextStyleSource.Local(),
  ) : GlobalValue<TextStyleSource> {
    override fun updateLabel(newLabel: String) = this.copy(label = newLabel)

    override fun updateInitialValue(value: TextStyleSource) = this.copy(initialValue = value)
  }
}

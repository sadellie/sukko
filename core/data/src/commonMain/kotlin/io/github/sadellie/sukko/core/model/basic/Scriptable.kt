package io.github.sadellie.sukko.core.model.basic

import androidx.compose.ui.graphics.Color
import io.github.sadellie.sukko.core.common.ColorSerializer
import io.github.sadellie.sukko.core.model.Globals
import kotlinx.serialization.Serializable

sealed interface Scriptable<V> {

  fun defaultValue(): V

  sealed interface Fixed<T, V> : Scriptable<V> {
    val value: T
  }

  sealed interface Script<T> : Scriptable<T> {
    val script: String
  }

  /** @property id id of global in [Globals] */
  sealed interface Global<V> : Scriptable<V> {
    val id: Long
  }
}

@Serializable
sealed interface ScriptableBoolean : Scriptable<Boolean> {
  override fun defaultValue() = false

  @Serializable
  data class Fixed(override val value: Boolean) :
    ScriptableBoolean, Scriptable.Fixed<Boolean, Boolean>

  @Serializable
  data class Script(override val script: String) : ScriptableBoolean, Scriptable.Script<Boolean>

  @Serializable
  data class Global(override val id: Long) : ScriptableBoolean, Scriptable.Global<Boolean>
}

@Serializable
sealed interface ScriptableString : Scriptable<String> {
  override fun defaultValue() = ""

  @Serializable
  data class Fixed(override val value: String) : ScriptableString, Scriptable.Fixed<String, String>

  @Serializable
  data class Script(override val script: String) : ScriptableString, Scriptable.Script<String>

  @Serializable
  data class Global(override val id: Long) : ScriptableString, Scriptable.Global<String>
}

@Serializable
sealed interface ScriptableDouble : Scriptable<Double> {
  override fun defaultValue() = 0.0

  @Serializable
  data class Fixed(override val value: Double) : ScriptableDouble, Scriptable.Fixed<Double, Double>

  @Serializable
  data class Script(override val script: String) : ScriptableDouble, Scriptable.Script<Double>

  @Serializable
  data class Global(override val id: Long) : ScriptableDouble, Scriptable.Global<Double>
}

@Serializable
sealed interface ScriptableColor : Scriptable<Color> {
  override fun defaultValue() = Color.Unspecified

  @Serializable
  data class FixedCustom(@Serializable(ColorSerializer::class) override val value: Color) :
    ScriptableColor, Scriptable.Fixed<Color, Color>

  @Serializable
  data class FixedM3(override val value: M3Color) :
    ScriptableColor, Scriptable.Fixed<M3Color, Color>

  @Serializable
  data class Script(override val script: String) : ScriptableColor, Scriptable.Script<Color>

  @Serializable data class Global(override val id: Long) : ScriptableColor, Scriptable.Global<Color>
}

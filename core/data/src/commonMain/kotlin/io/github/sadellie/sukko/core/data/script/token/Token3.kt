package io.github.sadellie.sukko.core.data.script.token

sealed interface Token3 {
  val symbol: String

  /**
   * Special marker for token that can override global values. Requires special flag to be enabled.
   */
  sealed interface GlobalOverrideAPI

  sealed interface Parentheses : Token3 {
    data object Left : Parentheses {
      override val symbol = "("
    }

    data object Right : Parentheses {
      override val symbol = ")"
    }
  }

  data object Comma : Token3 {
    override val symbol = ","
  }

  data class Number(override val symbol: String) : Token3

  data class Text(override val symbol: String) : Token3

  data class Variable(override val symbol: String) : Token3

  data object True : Token3 {
    override val symbol = "true"
  }

  data object False : Token3 {
    override val symbol = "false"
  }

  sealed interface Const : Token3 {
    data object BatteryLevel : Const {
      override val symbol = "batteryLevel"
    }

    data object BatteryStatus : Const {
      override val symbol = "batteryStatus"
    }

    data object MediaArtist : Const {
      override val symbol = "mediaArtist"
    }

    data object MediaTitle : Const {
      override val symbol = "mediaTitle"
    }

    data object MediaDuration : Const {
      override val symbol = "mediaDuration"
    }

    data object MediaPosition : Const {
      override val symbol = "mediaPosition"
    }

    data object MediaCover : Const {
      override val symbol = "mediaCover"
    }

    data object PlayerName : Const {
      override val symbol = "playerName"
    }

    data object PlayerIcon : Const {
      override val symbol = "playerIcon"
    }

    data object PlayerState : Const {
      override val symbol = "playerState"
    }

    data object DeviceModel : Const {
      override val symbol = "deviceModel"
    }

    data object BatteryFullEmpty : Const {
      override val symbol = "batteryFullEmpty"
    }

    data object CurrentTimestamp : Const {
      override val symbol = "currentTimestamp"
    }

    data object VolumeMusicMin : Const {
      override val symbol = "volumeMusicMin"
    }

    data object VolumeMusic : Const {
      override val symbol = "volumeMusic"
    }

    data object VolumeMusicMax : Const {
      override val symbol = "volumeMusicMax"
    }
  }

  sealed interface Function : Token3 {
    sealed interface Parameter {
      val name: String

      data class Required(override val name: String) : Parameter

      /** @param defaultValue Default value, script */
      data class Optional(override val name: String, val defaultValue: Token3? = null) :
        Parameter
    }

    val parameters: List<Parameter>
    val parametersCount
      get() = parameters.size

    data object If : Function {
      override val symbol = "if"
      override val parameters =
        listOf(
          Parameter.Required("condition"),
          Parameter.Required("ifTrue"),
          Parameter.Required("ifFalse"),
        )
    }

    data object CurrentDate : Function {
      override val symbol = "currentDate"
      override val parameters = listOf(Parameter.Required("format"))
    }

    data object CurrentDateWithTimeZone : Function {
      override val symbol = "currentDateWithTimeZone"
      override val parameters =
        listOf(Parameter.Required("format"), Parameter.Required("timezoneId"))
    }

    data object FormatTimestamp : Function {
      override val symbol = "formatTimestamp"
      override val parameters =
        listOf(Parameter.Required("timestamp"), Parameter.Required("format"))
    }

    data object DynamicColor : Function {
      override val symbol = "dynamicColor"
      override val parameters = listOf(Parameter.Required("colorName"))
    }

    data object ColorScheme : Function {
      override val symbol = "colorScheme"
      override val parameters =
        listOf(
          Parameter.Required("colorName"),
          Parameter.Required("source"),
          Parameter.Optional("scheme", Text("EXPRESSIVE")),
        )
    }

    data object GetGlobalString : Function {
      override val symbol = "globalString"
      override val parameters = listOf(Parameter.Required("id"))
    }

    data object GetGlobalNumber : Function {
      override val symbol = "globalNumber"
      override val parameters = listOf(Parameter.Required("id"))
    }

    data object GetGlobalBoolean : Function {
      override val symbol = "globalBoolean"
      override val parameters = listOf(Parameter.Required("id"))
    }

    data object SetGlobalNumber : Function, GlobalOverrideAPI {
      override val symbol = "setGlobalNumber"
      override val parameters =
        listOf(Parameter.Required("id"), Parameter.Required("value"))
    }

    data object SetGlobalString : Function, GlobalOverrideAPI {
      override val symbol = "setGlobalString"
      override val parameters =
        listOf(Parameter.Required("id"), Parameter.Required("value"))
    }

    data object SetGlobalBoolean : Function, GlobalOverrideAPI {
      override val symbol = "setGlobalBoolean"
      override val parameters =
        listOf(Parameter.Required("id"), Parameter.Required("value"))
    }
  }

  sealed interface Operator : Token3 {
    enum class Associativity {
      LEFT,
      RIGHT,
    }

    /** High value for earlier processing */
    val precedence: Int
    val associativity: Associativity
    val isUnary: Boolean

    data object Assign : Operator {
      override val symbol = "="
      // always last
      override val precedence = 0
      override val associativity = Associativity.LEFT
      override val isUnary = false
    }

    data object Or : Operator {
      override val symbol = "||"
      // after other operators before assign
      override val precedence = 1
      override val associativity = Associativity.LEFT
      override val isUnary = false
    }

    data object And : Operator {
      // after OR (from kotlin LSP)
      override val symbol = "&&"
      override val precedence = 2
      override val associativity = Associativity.LEFT
      override val isUnary = false
    }

    data object Equal : Operator {
      override val symbol = "=="
      override val precedence = 3
      override val associativity = Associativity.LEFT
      override val isUnary = false
    }

    data object NotEqual : Operator {
      override val symbol = "!="
      override val precedence = 3
      override val associativity = Associativity.LEFT
      override val isUnary = false
    }

    data object Less : Operator {
      override val symbol = "<"
      override val precedence = 3
      override val associativity = Associativity.LEFT
      override val isUnary = false
    }

    data object Greater : Operator {
      override val symbol = ">"
      override val precedence = 3
      override val associativity = Associativity.LEFT
      override val isUnary = false
    }

    data object LessOrEqual : Operator {
      override val symbol = "<="
      override val precedence = 3
      override val associativity = Associativity.LEFT
      override val isUnary = false
    }

    data object GreaterOrEqual : Operator {
      override val symbol = ">="
      override val precedence = 3
      override val associativity = Associativity.LEFT
      override val isUnary = false
    }

    data object Plus : Operator {
      override val symbol = "+"
      override val precedence = 4
      override val associativity = Associativity.LEFT
      override val isUnary = false
    }

    data object Minus : Operator {
      override val symbol = "-"
      override val precedence = 4
      override val associativity = Associativity.LEFT
      override val isUnary = false
    }

    data object Multiply : Operator {
      override val symbol = "*"
      override val precedence = 5
      override val associativity = Associativity.LEFT
      override val isUnary = false
    }

    data object Divide : Operator {
      override val symbol = "/"
      override val precedence = 5
      override val associativity = Associativity.LEFT
      override val isUnary = false
    }

    data object UnaryMinus : Operator {
      override val symbol = "-"
      // unary first
      override val precedence = 6
      override val associativity = Associativity.RIGHT
      override val isUnary = true
    }

    data object Not : Operator {
      override val symbol = "!"
      // unary first
      override val precedence = 6
      override val associativity = Associativity.RIGHT
      override val isUnary = true
    }
  }

  companion object {
    // do not include unary, handled as fixup in tokenizer method
    internal val allParseableTokens =
      listOf(
          Function.If,
          Function.CurrentDate,
          Function.CurrentDateWithTimeZone,
          Function.DynamicColor,
          Function.ColorScheme,
          Function.FormatTimestamp,
          Function.GetGlobalString,
          Function.GetGlobalNumber,
          Function.GetGlobalBoolean,
          Function.SetGlobalNumber,
          Function.SetGlobalString,
          Function.SetGlobalBoolean,
          Operator.Assign,
          Operator.Plus,
          Operator.Minus,
          Operator.Multiply,
          Operator.Divide,
          Operator.Equal,
          Operator.NotEqual,
          Operator.Less,
          Operator.Greater,
          Operator.LessOrEqual,
          Operator.GreaterOrEqual,
          Operator.Or,
          Operator.And,
          Operator.Not,
          Const.BatteryLevel,
          Const.BatteryStatus,
          Const.MediaArtist,
          Const.MediaTitle,
          Const.MediaDuration,
          Const.MediaPosition,
          Const.MediaCover,
          Const.PlayerName,
          Const.PlayerIcon,
          Const.PlayerState,
          Const.DeviceModel,
          Const.BatteryFullEmpty,
          Const.CurrentTimestamp,
          Const.VolumeMusicMin,
          Const.VolumeMusic,
          Const.VolumeMusicMax,
          Comma,
          True,
          False,
          Parentheses.Left,
          Parentheses.Right,
        )
        // tokenizer always looks for longest tokens first
        .sortedByDescending { it.symbol.length }
  }
}

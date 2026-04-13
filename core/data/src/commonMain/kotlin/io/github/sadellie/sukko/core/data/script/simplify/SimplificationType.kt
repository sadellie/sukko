package io.github.sadellie.sukko.core.data.script.simplify

internal enum class SimplificationType {
  INVISIBLE,
  SUM_OF_NUMBERS,
  PRODUCT_OF_NUMBERS,
  DIVISION_OF_NUMBERS,
  LOGICAL_OR,
  LOGICAL_AND,
  EQUAL,
  NOT_EQUAL,
  GREATER,
  GREATER_OR_EQUAL,
  LESS,
  LESS_OR_EQUAL,
  EVAL_CONSTANT,
  EVAL_FUNCTION,
}

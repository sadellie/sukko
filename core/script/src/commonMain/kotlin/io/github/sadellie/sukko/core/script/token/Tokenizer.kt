package io.github.sadellie.sukko.core.script.token

import io.github.sadellie.sukko.core.script.DOT

fun tokenize(input: String): List<Token3> {
  if (input.isEmpty()) return emptyList()
  var cursor = 0
  val tokens = mutableListOf<Token3>()
  // x = "\"test\""

  while (cursor != input.length) {
    // try to find token
    val tokenAhead = findTokenAhead(input, cursor)

    // if found add token to result and move cursor
    if (tokenAhead != null) {
      tokens.add(tokenAhead)
      cursor += tokenAhead.symbol.length
      continue
    }

    val charInFront = input[cursor]
    // not found maybe number if starts with a number
    if (charInFront.isDigitOrDot()) {
      // start with a number. go right until numbers end
      val numberTokenAhead = findNumberTokenAhead(input, cursor)
      // add number token to result and move cursor
      tokens.add(numberTokenAhead)
      cursor += numberTokenAhead.symbol.length
      continue
    }

    // maybe string if starts with quote
    // starts with quote, go right until second quote was hit, but jump over escapes
    // add string token to result and move cursor
    if (charInFront == QUOTE) {
      val quotedTokenAhead = findQuotedString(input, cursor)
      if (quotedTokenAhead != null) {
        val token = Token3.Text(quotedTokenAhead)
        tokens.add(token)
        // +2 for surrounding quotes that were dropped
        cursor += token.symbol.length + 2
        continue
      }
    }

    // maybe a variable if starts with a letter
    if (charInFront.isLetter()) {
      // starts with letter, go right until operator or space or quote
      val variableSymbol = input.substring(cursor).takeWhile { it.isLetterOrDigit() }
      tokens.add(Token3.Variable(variableSymbol))
      cursor += variableSymbol.length
      continue
    }

    cursor++
  }

  for ((index, token) in tokens.withIndex()) {
    if (token != Token3.Operator.Minus) continue

    // first symbol is always unary
    if (index == 0) {
      tokens[index] = Token3.Operator.UnaryMinus
      continue
    }

    // always unary when first in brackets
    // always unary when following another operator
    val previousToken = tokens[index - 1]
    if (previousToken is Token3.Parentheses.Left || previousToken is Token3.Operator) {
      tokens[index] = Token3.Operator.UnaryMinus
    }
  }

  return tokens
}

private fun findTokenAhead(input: String, cursor: Int): Token3? {
  for (token in Token3.allParseableTokens) {
    // look in front for possible match with current token
    val lookUpString =
      input.substring(cursor, (cursor + token.symbol.length).coerceAtMost(input.length))
    val isMatched = token.symbol == lookUpString
    if (isMatched) return token
  }
  return null
}

private fun findNumberTokenAhead(input: String, cursor: Int): Token3 {
  // walk left while
  val number = input.substring(cursor).takeWhile { char -> char.isDigitOrDot() }
  return Token3.Number(number)
}

private fun findQuotedString(input: String, cursor: Int): String? {
  val window = input.substring(cursor)
  val result = StringBuilder()
  var escapeNext = false
  var inQuotes = false
  window.forEach { char ->
    when {
      escapeNext -> {
        result.append(char)
        escapeNext = false
      }
      char == '\\' -> escapeNext = true
      char == '"' -> {
        if (inQuotes) return result.toString()
        inQuotes = true
      }
      inQuotes -> result.append(char)
    }
  }

  return null
}

private fun Char.isDigitOrDot(): Boolean = isDigit() || this == DOT

private const val QUOTE = '"'

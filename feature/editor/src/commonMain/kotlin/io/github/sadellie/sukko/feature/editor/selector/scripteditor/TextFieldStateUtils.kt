package io.github.sadellie.sukko.feature.editor.selector.scripteditor

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.text.TextRange

/** Paste [text]. Like pasting in any other app: insert at selection and update selection */
internal fun TextFieldState.insert(text: String) = edit {
  replace(selection.start, selection.end, text)
  selection = TextRange(selection.end)
}

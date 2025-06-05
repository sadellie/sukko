package io.github.sadellie.sukko.feature.editor.selector.scripteditor

internal sealed interface ScriptEditorPage {
  data object InputPage : ScriptEditorPage

  data object DocsPage : ScriptEditorPage
}

package io.github.sadellie.sukko.feature.editor.selector.scripteditor

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import google.material.design.symbols.Add
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.common.collectAsStateWithLifecycleKMP
import io.github.sadellie.sukko.core.data.script.docs.Docs
import io.github.sadellie.sukko.core.data.script.docs.DocsItem
import io.github.sadellie.sukko.core.data.script.docs.MethodParam
import io.github.sadellie.sukko.core.data.script.docs.ScriptTypeTag
import io.github.sadellie.sukko.core.data.script.docs.TranslatableString
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.ui.ListHeader
import io.github.sadellie.sukko.core.ui.ListItem2
import io.github.sadellie.sukko.core.ui.LoadingBox
import io.github.sadellie.sukko.core.ui.SearchBar
import io.github.sadellie.sukko.core.ui.listedShapes
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_selector_script_docs_boolean
import io.github.sadellie.sukko.resources.editor_selector_script_docs_number
import io.github.sadellie.sukko.resources.editor_selector_script_docs_text
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DocsPage(
  modifier: Modifier,
  viewModel: DocsViewModel,
  backToInput: () -> Unit,
  onInsert: (script: String) -> Unit,
) {
  val lang = Locale.current.language
  LaunchedEffect(Unit) { viewModel.observeSearchQuery(lang) }
  DocsPageContent(
    modifier = modifier,
    backToInput = backToInput,
    onInsert = onInsert,
    query = viewModel.searchTextFieldState,
    docs =
      viewModel.results
        .collectAsStateWithLifecycleKMP(Docs(emptyList(), emptyList(), emptyList()))
        .value,
  )
}

// TODO res headers
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DocsPageContent(
  modifier: Modifier,
  backToInput: () -> Unit,
  onInsert: (script: String) -> Unit,
  query: TextFieldState,
  docs: Docs?,
) {
  Column(modifier = modifier) {
    SearchBar(modifier = Modifier.fillMaxWidth(), state = query, navigateUp = backToInput)

    if (docs == null) {
      LoadingBox(modifier = Modifier.fillMaxSize().weight(1f))
    } else {
      DocsPageContentLoaded(modifier = Modifier.fillMaxWidth(), onInsert = onInsert, docs = docs)
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DocsPageContentLoaded(
  modifier: Modifier,
  onInsert: (script: String) -> Unit,
  docs: Docs,
) {
  LazyColumn(modifier = modifier, verticalArrangement = ListArrangement) {
    if (docs.templates.isNotEmpty()) {
      item { ListHeader("Templates", Modifier.padding(Sizes.small)) }
      itemsIndexed(docs.templates, key = { _, item -> "template-${item.script}" }) { index, item ->
        TemplateItemCard(
          modifier = Modifier,
          onInsert = onInsert,
          item = item,
          shapes = ListItemDefaults.listedShapes(index, docs.templates.size),
        )
      }
    }

    if (docs.constants.isNotEmpty()) {
      item { ListHeader("Constants", Modifier.padding(Sizes.small)) }
      itemsIndexed(docs.constants, key = { _, item -> "constant-${item.api}" }) { index, item ->
        ConstantItemCard(
          modifier = Modifier,
          onInsert = onInsert,
          item = item,
          shapes = ListItemDefaults.listedShapes(index, docs.constants.size),
        )
      }
    }

    if (docs.methods.isNotEmpty()) {
      item { ListHeader("Methods", Modifier.padding(Sizes.small)) }
      itemsIndexed(docs.methods, key = { _, item -> "method-${item.api}" }) { index, item ->
        MethodItemCard(
          modifier = Modifier,
          item = item,
          shapes = ListItemDefaults.listedShapes(index, docs.methods.size),
        )
      }
    }
  }
}

@Composable
private fun TemplateItemCard(
  modifier: Modifier,
  onInsert: (script: String) -> Unit,
  item: DocsItem.Template,
  shapes: ListItemShapes,
) {
  ListItem2(
    modifier = modifier,
    overlineContent = { Text(formatDocsItemName(item)) },
    content = {
      ScriptBlock(
        modifier = Modifier.padding(vertical = 2.dp).fillMaxWidth(),
        onClick = { onInsert(item.script) },
        script = item.script,
      )
    },
    shapes = shapes,
    onClick = {},
  )
}

@Composable
private fun ConstantItemCard(
  modifier: Modifier,
  onInsert: (String) -> Unit,
  item: DocsItem.Constant,
  shapes: ListItemShapes,
) {
  ListItem2(
    modifier = modifier,
    overlineContent = { Text(formatDocsItemName(item)) },
    content = {
      ScriptBlock(
        modifier = Modifier.padding(vertical = 2.dp).fillMaxWidth(),
        onClick = { onInsert(item.api) },
        script = item.api,
      )
    },
    supportingContent = { Text(item.description.load()) },
    shapes = shapes,
    onClick = {},
  )
}

@Composable
private fun MethodItemCard(modifier: Modifier, item: DocsItem.Method, shapes: ListItemShapes) {
  ListItem2(
    modifier = modifier,
    overlineContent = { Text(formatDocsItemName(item)) },
    content = {
      ScriptBlock(
        modifier = Modifier.padding(vertical = 2.dp).fillMaxWidth(),
        onClick = null,
        script = item.api,
      )
    },
    supportingContent = {
      Column {
        Text(item.description.load())
        item.params.forEach { param ->
          val types = param.types.map { it.displayName() }
          Text("${param.name} [${types.joinToString(", ")}] - ${param.description.load()}")
        }
      }
    },
    shapes = shapes,
    onClick = {},
  )
}

@Composable
private fun ScriptBlock(modifier: Modifier, onClick: (() -> Unit)? = null, script: String) {
  Row(
    modifier =
      modifier
        .clip(MaterialTheme.shapes.medium)
        .background(MaterialTheme.colorScheme.surfaceContainer)
        .padding(Sizes.extraSmall),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    SelectionContainer(modifier = Modifier.weight(1f)) {
      Text(
        text = script,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.horizontalScroll(rememberScrollState()).padding(Sizes.small),
        style = MaterialTheme.typography.bodySmall,
      )
    }
    if (onClick != null) {
      IconButton(
        onClick = onClick,
        shapes = IconButtonDefaults.shapes(),
        modifier = Modifier.size(IconButtonDefaults.extraSmallContainerSize()),
      ) {
        Icon(
          imageVector = Symbols.Add,
          contentDescription = null,
          modifier = Modifier.size(IconButtonDefaults.extraSmallIconSize),
        )
      }
    }
  }
}

@Composable
private fun formatDocsItemName(docs: DocsItem): String {
  val name = docs.name.load()
  val types = docs.returnTypes.map { it.displayName() }
  return remember(docs) { "$name (${types.joinToString(", ")})" }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun TranslatableString.load(): String {
  val language = Locale.current.language
  return remember(language, this) { this[language] ?: "" }
}

@Composable
private fun ScriptTypeTag.displayName() =
  stringResource(
    when (this) {
      ScriptTypeTag.TEXT -> Res.string.editor_selector_script_docs_text
      ScriptTypeTag.NUMBER -> Res.string.editor_selector_script_docs_number
      ScriptTypeTag.BOOL -> Res.string.editor_selector_script_docs_boolean
    }
  )

@Composable
@Preview
private fun PreviewDocsPageContent(@PreviewParameter(DocsPagePreviewProvider::class) docs: Docs?) =
  Preview2 {
    DocsPageContent(
      modifier = Modifier.padding(horizontal = Sizes.large),
      backToInput = {},
      onInsert = {},
      query = rememberTextFieldState(),
      docs = docs,
    )
  }

private class DocsPagePreviewProvider : PreviewParameterProvider<Docs?> {
  override val values =
    sequenceOf(
      null,
      Docs(constants = emptyList(), methods = emptyList(), templates = emptyList()),
      Docs(
        constants =
          List(4) {
            DocsItem.Constant(
              name = mapOf("en" to "Constant $it"),
              returnTypes = listOf(ScriptTypeTag.TEXT),
              description = mapOf("en" to "Constant description $it"),
              api = "api $it",
            )
          },
        methods =
          List(4) {
            DocsItem.Method(
              name = mapOf("en" to "Method $it"),
              returnTypes = listOf(ScriptTypeTag.TEXT),
              description = mapOf("en" to "Method description $it"),
              api = "api $it",
              params =
                List(2) { paramId ->
                  MethodParam(
                    name = "param$paramId",
                    description = mapOf("en" to "Param $paramId"),
                    types = listOf(ScriptTypeTag.NUMBER),
                  )
                },
            )
          },
        templates =
          List(4) {
            DocsItem.Template(
              name = mapOf("en" to "Template $it"),
              returnTypes = listOf(ScriptTypeTag.TEXT),
              script = "api $it",
            )
          },
      ),
    )
}

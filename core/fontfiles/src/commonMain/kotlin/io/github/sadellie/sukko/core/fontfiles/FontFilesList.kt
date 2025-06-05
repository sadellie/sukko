package io.github.sadellie.sukko.core.fontfiles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.ui.ListHeader
import io.github.sadellie.sukko.core.ui.ListItem2
import io.github.sadellie.sukko.core.ui.listedShape
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.font_files_list_custom
import io.github.sadellie.sukko.resources.font_files_list_preview
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FontFilesList(
  modifier: Modifier,
  custom: List<FontFile.Custom>,
  onClick: (FontFile) -> Unit,
  selectedFontFile: FontFile? = null,
  contentPadding: PaddingValues = PaddingValues(),
) {
  val builtIn = remember { FontFile.builtIn() }
  val fontFamilyLoader = remember { FontFamilyLoader() }
  LazyColumn(
    modifier = modifier,
    verticalArrangement = ListArrangement,
    contentPadding = contentPadding,
  ) {
    itemsIndexed(builtIn, { _, font -> font.id }) { index, fontFile ->
      FontFileListItem(
        modifier = Modifier.clickable { onClick(fontFile) },
        fontFile = fontFile,
        fontName = stringResource(fontFile.displayName),
        fontFamilyLoader = fontFamilyLoader,
        isSelected = selectedFontFile?.let { it == fontFile },
        shape = ListItemDefaults.listedShape(index, builtIn.size),
      )
    }

    if (custom.isNotEmpty()) {
      item("custom") { ListHeader(text = stringResource(Res.string.font_files_list_custom)) }

      itemsIndexed(custom, { _, font -> font.id }) { index, fontFile ->
        FontFileListItem(
          modifier = Modifier.clickable { onClick(fontFile) },
          fontFile = fontFile,
          fontName = fontFile.fileNameWithoutExtension,
          fontFamilyLoader = fontFamilyLoader,
          isSelected = selectedFontFile?.let { it == fontFile },
          shape = ListItemDefaults.listedShape(index, custom.size),
        )
      }
    }
  }
}

@Composable
private fun FontFileListItem(
  modifier: Modifier,
  fontName: String,
  fontFile: FontFile,
  fontFamilyLoader: FontFamilyLoader,
  isSelected: Boolean? = null,
  shape: Shape,
) {
  ListItem2(
    modifier = modifier,
    headlineContent = {
      Text(text = fontName, modifier = Modifier.padding(vertical = Sizes.extraSmall))
    },
    supportingContent = {
      Text(
        text = stringResource(Res.string.font_files_list_preview),
        fontFamily = fontFamilyLoader.rememberFontFamily(fontFile),
        modifier = Modifier.padding(vertical = Sizes.extraSmall),
      )
    },
    trailingContent = isSelected?.let { { RadioButton(selected = it, onClick = null) } },
    shape = shape,
  )
}

@Composable
@Preview
private fun PreviewFontFilesList() = Preview2 {
  FontFilesList(
    modifier = Modifier.fillMaxSize(),
    onClick = {},
    custom = List(5) { FontFile.Custom("font $it") },
  )
}

@Composable
@Preview
private fun PreviewFontFilesList2() = Preview2 {
  FontFilesList(
    modifier = Modifier.fillMaxSize(),
    onClick = {},
    custom = List(5) { FontFile.Custom("font $it") },
    selectedFontFile = FontFile.Serif,
  )
}

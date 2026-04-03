package io.github.sadellie.sukko.feature.editor.parameters

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import io.github.sadellie.sukko.core.common.uri
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.basic.ImageUriSource
import io.github.sadellie.sukko.core.model.layer.ColdImageLayer
import io.github.sadellie.sukko.core.ui.ExpandableListItem
import io.github.sadellie.sukko.core.ui.ListItem2Compact
import io.github.sadellie.sukko.core.ui.ModalBottomSheetWithItems
import io.github.sadellie.sukko.core.ui.expand
import io.github.sadellie.sukko.core.ui.middleShapes
import io.github.sadellie.sukko.feature.editor.selector.IconSelectorSheet
import io.github.sadellie.sukko.feature.editor.selector.StringSelectorSheet
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.editor_parameters_icon
import io.github.sadellie.sukko.resources.editor_parameters_image_uri_gallery_custom_value
import io.github.sadellie.sukko.resources.editor_parameters_image_uri_gallery_selected_image
import io.github.sadellie.sukko.resources.editor_parameters_image_uri_source
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import org.jetbrains.compose.resources.stringResource

@Composable
internal actual fun EditorParametersImageUri(
  onUpdateLayer: (ColdImageLayer) -> Unit,
  layer: ColdImageLayer,
  compactListMode: Boolean,
  globals: Globals,
  shapes: ListItemShapes,
) {
  ExpandableListItem(
    headlineText = stringResource(Res.string.editor_parameters_image_uri_source),
    supportingText = layer.imageUriSource.displayValue(),
    compactListMode = compactListMode,
    shapes = ListItemDefaults.middleShapes,
  ) {
    val uriSourceSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
    ListItem2Compact(
      content = { Text(stringResource(Res.string.editor_parameters_image_uri_source)) },
      supportingContent = { Text(stringResource(layer.imageUriSource.displayName)) },
      compactListMode = compactListMode,
      onClick = uriSourceSheetState::expand,
      shapes = ListItemDefaults.middleShapes,
    )
    AnimatedContent(layer.imageUriSource) { imageUriSource ->
      when (imageUriSource) {
        is ImageUriSource.Gallery ->
          EditorParameterGallerySource(
            imageUriSource = imageUriSource,
            onUpdate = { onUpdateLayer(layer.copy(imageUriSource = it)) },
            compactListMode = compactListMode,
          )
        is ImageUriSource.Link ->
          EditorParameterLinkSource(
            imageUriSource = imageUriSource,
            onUpdate = { onUpdateLayer(layer.copy(imageUriSource = it)) },
            compactListMode = compactListMode,
            globals = globals,
          )
        is ImageUriSource.IconPack ->
          EditorParameterIconPackSource(
            imageUriSource = imageUriSource,
            onUpdate = { onUpdateLayer(layer.copy(imageUriSource = it)) },
            compactListMode = compactListMode,
          )
        ImageUriSource.PlayerIcon,
        ImageUriSource.AlbumCover -> Unit
      }
    }

    ModalBottomSheetWithItems(
      state = uriSourceSheetState,
      items = remember { ImageUriSource.values() },
      headlineText = { stringResource(it.displayName) },
      onClick = { onUpdateLayer(layer.copy(imageUriSource = it)) },
    )
  }
}

@Composable
private fun EditorParameterGallerySource(
  imageUriSource: ImageUriSource.Gallery,
  onUpdate: (ImageUriSource.Gallery) -> Unit,
  compactListMode: Boolean,
) {
  val context = LocalContext.current
  val imagePicker =
    rememberFilePickerLauncher(FileKitType.Image) {
      if (it == null) return@rememberFilePickerLauncher
      val pickedFileUri = it.uri()
      context.contentResolver.takePersistableUriPermission(
        pickedFileUri,
        Intent.FLAG_GRANT_READ_URI_PERMISSION,
      )

      onUpdate(ImageUriSource.Gallery(pickedFileUri.toString()))
    }
  ListItem2Compact(
    content = {
      Text(stringResource(Res.string.editor_parameters_image_uri_gallery_selected_image))
    },
    supportingContent = { Text(imageUriSource.displayValue()) },
    compactListMode = compactListMode,
    onClick = imagePicker::launch,
    shapes = ListItemDefaults.middleShapes,
  )
}

@Composable
private fun EditorParameterLinkSource(
  imageUriSource: ImageUriSource.Link,
  onUpdate: (ImageUriSource.Link) -> Unit,
  compactListMode: Boolean,
  globals: Globals,
) {
  val customUriSheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(Res.string.editor_parameters_image_uri_gallery_custom_value)) },
    supportingContent = { Text(imageUriSource.displayValue()) },
    compactListMode = compactListMode,
    onClick = customUriSheetState::expand,
    shapes = ListItemDefaults.middleShapes,
  )
  StringSelectorSheet(
    state = customUriSheetState,
    onValueSelected = { onUpdate(imageUriSource.copy(value = it)) },
    value = imageUriSource.value,
    globals = globals.strings,
  )
}

@Composable
private fun EditorParameterIconPackSource(
  imageUriSource: ImageUriSource.IconPack,
  onUpdate: (ImageUriSource.IconPack) -> Unit,
  compactListMode: Boolean,
) {
  val sheetState = rememberModalBottomSheetState(SheetDetent.Hidden)
  ListItem2Compact(
    content = { Text(stringResource(Res.string.editor_parameters_icon)) },
    supportingContent = { Text(imageUriSource.displayValue()) },
    compactListMode = compactListMode,
    onClick = { sheetState.expand() },
    shapes = ListItemDefaults.middleShapes,
  )
  IconSelectorSheet(
    state = sheetState,
    onValueSelected = { onUpdate(ImageUriSource.IconPack(it)) },
    value = imageUriSource.iconFile,
  )
}

package io.github.sadellie.sukko.feature.editor.selector.scripteditor

import android.content.Intent
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import google.material.design.symbols.AddPhotoAlternate
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.common.uri
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher

@Composable
internal actual fun AddImageLinkButton(onAdd: (String) -> Unit) {
  val context = LocalContext.current
  val launcher = rememberFilePickerLauncher { file ->
    if (file == null) return@rememberFilePickerLauncher
    val pickedFileUri = file.uri()
    context.contentResolver.takePersistableUriPermission(
      pickedFileUri,
      Intent.FLAG_GRANT_READ_URI_PERMISSION,
    )
    onAdd(pickedFileUri.toString())
  }
  IconButton(
    modifier =
      Modifier.size(
        IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Uniform)
      ),
    onClick = launcher::launch,
    shapes = IconButtonDefaults.shapes(),
  ) {
    Icon(
      imageVector = Symbols.AddPhotoAlternate,
      contentDescription = null,
      modifier = Modifier.size(IconButtonDefaults.smallIconSize),
    )
  }
}

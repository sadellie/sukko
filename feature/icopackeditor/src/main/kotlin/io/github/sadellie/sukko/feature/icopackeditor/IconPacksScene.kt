package io.github.sadellie.sukko.feature.icopackeditor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import google.material.design.symbols.Add
import google.material.design.symbols.DeleteForever
import google.material.design.symbols.Edit
import google.material.design.symbols.Symbols
import io.github.sadellie.sukko.core.common.collectAsStateWithLifecycleKMP
import io.github.sadellie.sukko.core.common.stateIn
import io.github.sadellie.sukko.core.data.IconPackCustomRepository
import io.github.sadellie.sukko.core.designsystem.Preview2
import io.github.sadellie.sukko.core.designsystem.theme.ListArrangement
import io.github.sadellie.sukko.core.designsystem.theme.Sizes
import io.github.sadellie.sukko.core.iconfiles.IconPack
import io.github.sadellie.sukko.core.ui.AlertDialogWithText
import io.github.sadellie.sukko.core.ui.AlertDialogWithTextField
import io.github.sadellie.sukko.core.ui.DropDownMenuWithButton
import io.github.sadellie.sukko.core.ui.EmptyScreen
import io.github.sadellie.sukko.core.ui.ListHeader
import io.github.sadellie.sukko.core.ui.ListItem2
import io.github.sadellie.sukko.core.ui.NavigateUpButton
import io.github.sadellie.sukko.core.ui.ScaffoldWithLargeTopAppBar
import io.github.sadellie.sukko.core.ui.listedShape
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.common_create_new
import io.github.sadellie.sukko.resources.common_delete
import io.github.sadellie.sukko.resources.common_rename
import io.github.sadellie.sukko.resources.icon_packs_editor_delete_text
import io.github.sadellie.sukko.resources.icon_packs_editor_delete_title
import io.github.sadellie.sukko.resources.icon_packs_editor_icon_pack_name
import io.github.sadellie.sukko.resources.icon_packs_editor_my_icon_packs
import io.github.sadellie.sukko.resources.icon_packs_editor_rename
import io.github.sadellie.sukko.resources.icon_packs_editor_title
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Serializable data object IconPacksListEditorRoute : NavKey

@Composable
fun IconPacksScene(onNavigateUp: () -> Unit, navigateToIconPackEditor: (IconPack) -> Unit) {
  val viewModel = koinViewModel<IconPacksViewModel>()
  val iconPacks = viewModel.iconPacks.collectAsStateWithLifecycleKMP().value

  if (iconPacks == null) {
    EmptyScreen()
  } else {
    IconPacksScreen(
      onNavigateUp = onNavigateUp,
      onDelete = viewModel::delete,
      onRename = viewModel::rename,
      onCreateNew = viewModel::add,
      onSelect = navigateToIconPackEditor,
      iconPacks = iconPacks,
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun IconPacksScreen(
  onNavigateUp: () -> Unit,
  onDelete: (IconPack.Custom) -> Unit,
  onRename: (IconPack.Custom, newName: String) -> Unit,
  onCreateNew: (IconPack.Custom) -> Unit,
  onSelect: (IconPack) -> Unit,
  iconPacks: List<IconPack.Custom>,
) {
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
  var alertState by remember { mutableStateOf<IconPacksAlertState?>(null) }
  ScaffoldWithLargeTopAppBar(
    title = { Text(stringResource(Res.string.icon_packs_editor_title)) },
    navigationIcon = { NavigateUpButton(onNavigateUp) },
    floatingActionButton = {
      LargeFloatingActionButton(onClick = { alertState = IconPacksAlertState.CreateNew }) {
        Icon(
          imageVector = Symbols.Add,
          contentDescription = null,
          modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize),
        )
      }
    },
    floatingActionButtonPosition = FabPosition.Center,
    scrollBehavior = scrollBehavior,
  ) { paddingValues ->
    val builtIns = remember { IconPack.builtIns() }
    LazyColumn(
      modifier =
        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
          .padding(paddingValues)
          .fillMaxSize(),
      // 124.dp for large FAB
      contentPadding = PaddingValues(bottom = 124.dp, start = Sizes.large, end = Sizes.large),
      verticalArrangement = ListArrangement,
    ) {
      itemsIndexed(
        items = builtIns,
        key = { _, iconPack -> iconPack.iconPackId },
        contentType = { _, _ -> IconPackContentType.ICON_PACK },
      ) { index, iconPack ->
        ListItem2(
          headlineContent = { Text(iconPack.name) },
          modifier = Modifier.clickable { onSelect(iconPack) },
          shape = ListItemDefaults.listedShape(index, builtIns.size),
        )
      }

      if (iconPacks.isNotEmpty()) {
        item(key = "custom.header", contentType = IconPackContentType.HEADER) {
          ListHeader(text = stringResource(Res.string.icon_packs_editor_my_icon_packs))
        }

        itemsIndexed(
          items = iconPacks,
          key = { _, iconPack -> iconPack.iconPackId },
          contentType = { _, _ -> IconPackContentType.ICON_PACK },
        ) { index, iconPack ->
          CustomIconPackListItem(
            modifier = Modifier.clickable { onSelect(iconPack) },
            onRenameMenuClick = { alertState = IconPacksAlertState.Rename(iconPack) },
            onDeleteMenuClick = { alertState = IconPacksAlertState.Delete(iconPack) },
            iconPack = iconPack,
            shape = ListItemDefaults.listedShape(index, iconPacks.size),
          )
        }
      }
    }
  }

  when (val state = alertState) {
    IconPacksAlertState.CreateNew ->
      AlertDialogWithTextField(
        onDismiss = { alertState = null },
        onConfirm = { onCreateNew(IconPack.Custom(0, it)) },
        icon = Symbols.Add,
        title = stringResource(Res.string.common_create_new),
        textFieldLabel = stringResource(Res.string.icon_packs_editor_icon_pack_name),
      )
    is IconPacksAlertState.Delete ->
      AlertDialogWithText(
        onDismiss = { alertState = null },
        onConfirm = { onDelete(state.iconPack) },
        icon = Symbols.DeleteForever,
        title = stringResource(Res.string.icon_packs_editor_delete_title),
        text = stringResource(Res.string.icon_packs_editor_delete_text),
      )
    is IconPacksAlertState.Rename ->
      AlertDialogWithTextField(
        onDismiss = { alertState = null },
        onConfirm = { onRename(state.iconPack, it) },
        icon = Symbols.Edit,
        textFieldState = rememberTextFieldState(state.iconPack.name),
        title = stringResource(Res.string.icon_packs_editor_rename),
        textFieldLabel = stringResource(Res.string.icon_packs_editor_icon_pack_name),
      )
    null -> Unit
  }
}

@Composable
private fun CustomIconPackListItem(
  modifier: Modifier,
  onDeleteMenuClick: () -> Unit,
  onRenameMenuClick: () -> Unit,
  iconPack: IconPack.Custom,
  shape: Shape,
) {
  ListItem2(
    headlineContent = { Text(iconPack.name) },
    trailingContent = {
      DropDownMenuWithButton {
        DropdownMenuItem(
          text = { Text(stringResource(Res.string.common_rename)) },
          leadingIcon = { Icon(Symbols.Edit, contentDescription = null) },
          onClick = onRenameMenuClick,
        )
        DropdownMenuItem(
          text = { Text(stringResource(Res.string.common_delete)) },
          leadingIcon = { Icon(Symbols.DeleteForever, contentDescription = null) },
          onClick = onDeleteMenuClick,
        )
      }
    },
    modifier = modifier,
    shape = shape,
  )
}

class IconPacksViewModel(private val iconPackCustomRepository: IconPackCustomRepository) :
  ViewModel() {
  val iconPacks = iconPackCustomRepository.getAll().stateIn(viewModelScope, null)

  fun delete(iconPack: IconPack.Custom) =
    viewModelScope.launch { iconPackCustomRepository.deleteIconPack(iconPack) }

  fun rename(iconPack: IconPack.Custom, newName: String) =
    viewModelScope.launch { iconPackCustomRepository.renameIconPack(iconPack, newName) }

  fun add(iconPack: IconPack.Custom) =
    viewModelScope.launch { iconPackCustomRepository.addNewIconPack(iconPack) }
}

private sealed interface IconPacksAlertState {
  data class Delete(val iconPack: IconPack.Custom) : IconPacksAlertState

  data class Rename(val iconPack: IconPack.Custom) : IconPacksAlertState

  data object CreateNew : IconPacksAlertState
}

private enum class IconPackContentType {
  HEADER,
  ICON_PACK,
}

@Composable
@Preview
private fun PreviewIconPacksScreen(
  @PreviewParameter(IconPacksCollection::class) iconPacks: List<IconPack.Custom>
) = Preview2 {
  IconPacksScreen(
    onNavigateUp = {},
    onDelete = {},
    onRename = { _, _ -> },
    onCreateNew = {},
    onSelect = {},
    iconPacks = iconPacks,
  )
}

@Suppress("MagicNumber")
private class IconPacksCollection(
  override val values: Sequence<List<IconPack>> =
    sequenceOf(
      List(20) { IconPack.Custom(iconPackId = it.toLong(), name = "Icon pack $it") },
      emptyList(),
    )
) : PreviewParameterProvider<List<IconPack>>

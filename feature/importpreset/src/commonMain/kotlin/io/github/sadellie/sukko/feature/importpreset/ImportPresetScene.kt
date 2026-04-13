package io.github.sadellie.sukko.feature.importpreset

import androidx.compose.runtime.Composable

@Composable
internal expect fun ImportPresetScene(navigateUp: () -> Unit, importingPresetUri: String)

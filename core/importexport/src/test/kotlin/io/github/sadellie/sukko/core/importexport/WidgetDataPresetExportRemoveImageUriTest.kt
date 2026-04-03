package io.github.sadellie.sukko.core.importexport

import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.WidgetDataPreset
import io.github.sadellie.sukko.core.model.basic.ImageUriSource
import io.github.sadellie.sukko.core.model.layer.ColdColumnLayer
import io.github.sadellie.sukko.core.model.layer.ColdImageLayer
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import org.junit.Test
import kotlin.test.assertEquals

class WidgetDataPresetExportRemoveImageUriTest {
  @Test
  fun nullifyImageUri_noLayers() {
    val widgetDataPreset =
      WidgetDataPreset.Custom(
        presetId = 18,
        name = "Widget preset 18",
        layers = emptyList(),
        globals = Globals(),
      )
    val expected =
      WidgetDataPreset.Custom(
        presetId = 18,
        name = "Widget preset 18",
        layers = emptyList(),
        globals = Globals(),
      )
    val actual = widgetDataPreset.nullifyGalleryImageUri()
    assertEquals(expected, actual)
  }

  @Test
  fun nullifyImageUri_noImageLayers() {
    val widgetDataPreset =
      WidgetDataPreset.Custom(
        presetId = 18,
        name = "Widget preset 18",
        layers =
          listOf(
            ColdColumnLayer(id = 0),
            ColdTextLayer(id = 1, parentId = 0),
            ColdTextLayer(id = 2, parentId = 0),
            ColdImageLayer(
              id = 3,
              parentId = 0,
              imageUriSource = ImageUriSource.IconPack(iconFile = null),
            ),
          ),
        globals = Globals(),
      )
    val expected =
      WidgetDataPreset.Custom(
        presetId = 18,
        name = "Widget preset 18",
        layers =
          listOf(
            ColdColumnLayer(id = 0),
            ColdTextLayer(id = 1, parentId = 0),
            ColdTextLayer(id = 2, parentId = 0),
            ColdImageLayer(
              id = 3,
              parentId = 0,
              imageUriSource = ImageUriSource.IconPack(iconFile = null),
            ),
          ),
        globals = Globals(),
      )
    val actual = widgetDataPreset.nullifyGalleryImageUri()
    assertEquals(expected, actual)
  }

  @Test
  fun nullifyImageUri_withImageLayers() {
    val widgetDataPreset =
      WidgetDataPreset.Custom(
        presetId = 18,
        name = "Widget preset 18",
        layers =
          listOf(
            ColdColumnLayer(id = 0),
            ColdTextLayer(id = 1, parentId = 0),
            ColdTextLayer(id = 2, parentId = 0),
            ColdImageLayer(
              id = 3,
              parentId = 0,
              imageUriSource = ImageUriSource.Gallery("content://uri"),
            ),
          ),
        globals = Globals(),
      )
    val expected =
      WidgetDataPreset.Custom(
        presetId = 18,
        name = "Widget preset 18",
        layers =
          listOf(
            ColdColumnLayer(id = 0),
            ColdTextLayer(id = 1, parentId = 0),
            ColdTextLayer(id = 2, parentId = 0),
            ColdImageLayer(id = 3, parentId = 0, imageUriSource = ImageUriSource.Gallery(null)),
          ),
        globals = Globals(),
      )
    val actual = widgetDataPreset.nullifyGalleryImageUri()
    assertEquals(expected, actual)
  }
}

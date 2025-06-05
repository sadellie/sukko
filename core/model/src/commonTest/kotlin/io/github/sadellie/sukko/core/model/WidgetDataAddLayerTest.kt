package io.github.sadellie.sukko.core.model

import io.github.sadellie.sukko.core.model.layer.ColdColumnLayer
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import kotlin.test.Test
import kotlin.test.assertEquals

class WidgetDataAddLayerTest {
  @Test
  fun addLayer_empty() {
    val widgetData = WidgetData(appWidgetId = 1)
    val expected = WidgetData(appWidgetId = 1, layers = listOf(ColdTextLayer(0)))
    val actual = widgetData.addLayer(ColdTextLayer(id = 1, parentId = null))
    assertEquals(expected, actual)
  }

  @Test
  fun addLayer_invalidParentId() {
    val widgetData =
      WidgetData(
        appWidgetId = 1,
        layers =
          listOf(
            ColdColumnLayer(id = 0),
            ColdColumnLayer(id = 1),
            ColdColumnLayer(id = 2, parentId = 1),
            ColdTextLayer(id = 3, parentId = 2),
            ColdTextLayer(id = 3, parentId = 2),
            ColdColumnLayer(id = 4),
            ColdTextLayer(id = 5, parentId = 4),
            ColdTextLayer(id = 6),
          ),
      )
    val expected =
      WidgetData(
        appWidgetId = 1,
        layers =
          listOf(
            ColdColumnLayer(id = 0),
            ColdColumnLayer(id = 1),
            ColdColumnLayer(id = 2, parentId = 1),
            ColdTextLayer(id = 3, parentId = 2),
            ColdTextLayer(id = 3, parentId = 2),
            ColdColumnLayer(id = 4),
            ColdTextLayer(id = 5, parentId = 4),
            ColdTextLayer(id = 6),
          ),
      )
    val actual = widgetData.addLayer(ColdTextLayer(id = 0, parentId = 999))
    assertEquals(expected, actual)
  }

  @Test
  fun addLayer_validParentId() {
    val widgetData =
      WidgetData(
        appWidgetId = 1,
        layers =
          listOf(
            ColdColumnLayer(id = 0),
            ColdColumnLayer(id = 1),
            ColdColumnLayer(id = 2, parentId = 1),
            ColdTextLayer(id = 3, parentId = 2),
            ColdTextLayer(id = 3, parentId = 2),
            ColdColumnLayer(id = 4),
            ColdTextLayer(id = 5, parentId = 4),
            ColdTextLayer(id = 6),
          ),
      )
    val expected =
      WidgetData(
        appWidgetId = 1,
        layers =
          listOf(
            ColdColumnLayer(id = 0),
            ColdColumnLayer(id = 1),
            ColdColumnLayer(id = 2, parentId = 1),
            ColdTextLayer(id = 3, parentId = 2),
            ColdTextLayer(id = 3, parentId = 2),
            ColdColumnLayer(id = 4),
            ColdTextLayer(id = 5, parentId = 4),
            ColdTextLayer(id = 6),
            ColdTextLayer(id = 7, parentId = 2),
          ),
      )
    val actual = widgetData.addLayer(ColdTextLayer(id = 0, parentId = 2))
    assertEquals(expected, actual)
  }

  @Test
  fun addLayer_AddToRoot() {
    val widgetData =
      WidgetData(
        appWidgetId = 1,
        layers =
          listOf(
            ColdColumnLayer(id = 0, parentId = null),
            ColdColumnLayer(id = 1, parentId = null),
            ColdColumnLayer(id = 2, parentId = 1),
            ColdTextLayer(id = 3, parentId = 2),
            ColdTextLayer(id = 3, parentId = 2),
            ColdColumnLayer(id = 4, parentId = null),
            ColdTextLayer(id = 5, parentId = 4),
            ColdTextLayer(id = 6, parentId = null),
          ),
      )
    val expected =
      WidgetData(
        appWidgetId = 1,
        layers =
          listOf(
            ColdColumnLayer(id = 0, parentId = null),
            ColdColumnLayer(id = 1, parentId = null),
            ColdColumnLayer(id = 2, parentId = 1),
            ColdTextLayer(id = 3, parentId = 2),
            ColdTextLayer(id = 3, parentId = 2),
            ColdColumnLayer(id = 4, parentId = null),
            ColdTextLayer(id = 5, parentId = 4),
            ColdTextLayer(id = 6, parentId = null),
            ColdTextLayer(id = 7, parentId = null),
          ),
      )
    val actual = widgetData.addLayer(ColdTextLayer(id = 0, parentId = null))
    assertEquals(expected, actual)
  }
}

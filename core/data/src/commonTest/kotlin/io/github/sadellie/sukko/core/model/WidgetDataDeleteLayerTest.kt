package io.github.sadellie.sukko.core.model

import io.github.sadellie.sukko.core.model.layer.ColdColumnLayer
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import kotlin.test.Test
import kotlin.test.assertEquals

class WidgetDataDeleteLayerTest {
  @Test
  fun deleteLayer_empty() {
    val widgetData = WidgetData(appWidgetId = 1)
    val expected = WidgetData(appWidgetId = 1)
    val actual = widgetData.deleteLayer(id = 1)
    assertEquals(expected, actual)
  }

  @Test
  fun deleteLayer_invalidLayer() {
    val widgetData =
      WidgetData(
        appWidgetId = 1,
        layers = listOf(ColdTextLayer(id = 1), ColdTextLayer(id = 2), ColdTextLayer(id = 3)),
      )
    val expected =
      WidgetData(
        appWidgetId = 1,
        layers = listOf(ColdTextLayer(id = 1), ColdTextLayer(id = 2), ColdTextLayer(id = 3)),
      )
    val actual = widgetData.deleteLayer(id = 4)
    assertEquals(expected, actual)
  }

  @Test
  fun deleteLayer_noChildren() {
    val widgetData =
      WidgetData(
        appWidgetId = 1,
        layers = listOf(ColdTextLayer(id = 1), ColdTextLayer(id = 2), ColdTextLayer(id = 3)),
      )
    val expected =
      WidgetData(appWidgetId = 1, layers = listOf(ColdTextLayer(id = 1), ColdTextLayer(id = 3)))
    val actual = widgetData.deleteLayer(id = 2)
    assertEquals(expected, actual)
  }

  @Test
  fun deleteLayer_withChildren() {
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
            ColdColumnLayer(id = 4),
            ColdTextLayer(id = 5, parentId = 4),
            ColdTextLayer(id = 6),
          ),
      )
    val actual = widgetData.deleteLayer(id = 1)
    assertEquals(expected, actual)
  }
}

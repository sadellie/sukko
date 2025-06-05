package io.github.sadellie.sukko.core.model

import io.github.sadellie.sukko.core.model.layer.ColdColumnLayer
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import kotlin.test.Test
import kotlin.test.assertEquals

class WidgetDataUpdateLayerOrderTest {
  @Test
  fun updateLayerOrder_empty() {
    val widgetData = WidgetData(appWidgetId = 1)
    val expected = WidgetData(appWidgetId = 1)
    val actual = widgetData.updateLayerOrder(listOf(ColdTextLayer(id = 1), ColdColumnLayer(id = 2)))
    assertEquals(expected, actual)
  }

  @Test
  fun updateLayerOrder_noNested() {
    val widgetData =
      WidgetData(
        appWidgetId = 1,
        layers = listOf(ColdTextLayer(id = 1), ColdColumnLayer(id = 2), ColdTextLayer(id = 3)),
      )
    val expected =
      WidgetData(
        appWidgetId = 1,
        layers = listOf(ColdColumnLayer(id = 2), ColdTextLayer(id = 3), ColdTextLayer(id = 1)),
      )
    val actual =
      widgetData.updateLayerOrder(
        listOf(ColdColumnLayer(id = 2), ColdTextLayer(id = 3), ColdTextLayer(id = 1))
      )
    assertEquals(expected, actual)
  }

  @Test
  fun updateLayerOrder_withNested() {
    val widgetData =
      WidgetData(
        appWidgetId = 1,
        layers =
          listOf(
            ColdTextLayer(id = 1),
            ColdColumnLayer(id = 2),
            ColdTextLayer(id = 3),
            ColdTextLayer(id = 4, parentId = 2),
            ColdTextLayer(id = 5, parentId = 2),
            ColdTextLayer(id = 6, parentId = 2),
            ColdTextLayer(id = 7),
          ),
      )
    val expected =
      WidgetData(
        appWidgetId = 1,
        layers =
          listOf(
            ColdTextLayer(id = 1),
            ColdColumnLayer(id = 2),
            ColdTextLayer(id = 3),
            ColdTextLayer(id = 7),
            ColdTextLayer(id = 6, parentId = 2),
            ColdTextLayer(id = 5, parentId = 2),
            ColdTextLayer(id = 4, parentId = 2),
          ),
      )
    val actual =
      widgetData.updateLayerOrder(
        listOf(
          ColdTextLayer(id = 6, parentId = 2),
          ColdTextLayer(id = 5, parentId = 2),
          ColdTextLayer(id = 4, parentId = 2),
        )
      )
    assertEquals(expected, actual)
  }
}

package io.github.sadellie.sukko.core.model

import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import kotlin.test.Test
import kotlin.test.assertEquals

class WidgetDataUpdateLayerTest {
  @Test
  fun updateLayer_empty() {
    val widgetData = WidgetData(appWidgetId = 1)
    val expected = WidgetData(appWidgetId = 1)
    val actual = widgetData.updateLayer(ColdTextLayer(id = 100))
    assertEquals(expected, actual)
  }

  @Test
  fun updateLayer_invalidLayer() {
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
    val actual = widgetData.updateLayer(ColdTextLayer(id = 100, name = "invalid layer"))
    assertEquals(expected, actual)
  }

  @Test
  fun updateLayer_test1() {
    val widgetData =
      WidgetData(
        appWidgetId = 1,
        layers = listOf(ColdTextLayer(id = 1), ColdTextLayer(id = 2), ColdTextLayer(id = 3)),
      )
    val expected =
      WidgetData(
        appWidgetId = 1,
        layers =
          listOf(
            ColdTextLayer(id = 1),
            ColdTextLayer(id = 2, name = "updated layer"),
            ColdTextLayer(id = 3),
          ),
      )
    val actual = widgetData.updateLayer(ColdTextLayer(id = 2, name = "updated layer"))
    assertEquals(expected, actual)
  }
}

package io.github.sadellie.sukko.core.model

import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.sadellie.sukko.core.model.modifier.ColdWidthModifier
import kotlin.test.Test
import kotlin.test.assertEquals

class WidgetDataUpdateWidgetModifierTest {
  @Test
  fun updateWidgetModifier_emptyLayers() {
    val widgetData = WidgetData(appWidgetId = 1)
    val expected = WidgetData(appWidgetId = 1)
    val actual =
      widgetData.updateWidgetModifier(layerId = 1, widgetModifier = ColdWidthModifier(id = 0))
    assertEquals(expected, actual)
  }

  @Test
  fun updateWidgetModifier_emptyWidgetModifiers() {
    val widgetData =
      WidgetData(
        appWidgetId = 1,
        layers = listOf(ColdTextLayer(id = 0, widgetModifiers = emptyList())),
      )
    val expected =
      WidgetData(
        appWidgetId = 1,
        layers = listOf(ColdTextLayer(id = 0, widgetModifiers = emptyList())),
      )
    val actual =
      widgetData.updateWidgetModifier(layerId = 0, widgetModifier = ColdWidthModifier(id = 0))
    assertEquals(expected, actual)
  }

  @Test
  fun addWidgetModifier_test1() {
    val widgetData =
      WidgetData(
        appWidgetId = 1,
        layers =
          listOf(
            ColdTextLayer(
              id = 0,
              widgetModifiers =
                listOf(
                  ColdWidthModifier(id = 0),
                  ColdWidthModifier(id = 1),
                  ColdWidthModifier(id = 2),
                ),
            )
          ),
      )
    val expected =
      WidgetData(
        appWidgetId = 1,
        layers =
          listOf(
            ColdTextLayer(
              id = 0,
              widgetModifiers =
                listOf(
                  ColdWidthModifier(id = 0),
                  ColdWidthModifier(id = 1),
                  ColdWidthModifier(id = 2),
                ),
            )
          ),
      )
    val actual =
      widgetData.updateWidgetModifier(layerId = 0, widgetModifier = ColdWidthModifier(id = 2))
    assertEquals(expected, actual)
  }
}

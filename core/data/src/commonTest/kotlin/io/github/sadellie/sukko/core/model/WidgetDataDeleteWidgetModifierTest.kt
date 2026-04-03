package io.github.sadellie.sukko.core.model

import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.sadellie.sukko.core.model.modifier.ColdWidthModifier
import kotlin.test.Test
import kotlin.test.assertEquals

class WidgetDataDeleteWidgetModifierTest {
  @Test
  fun deleteWidgetModifier_emptyLayers() {
    val widgetData = WidgetData(appWidgetId = 1)
    val expected = WidgetData(appWidgetId = 1)
    val actual =
      widgetData.deleteWidgetModifier(layerId = 1, widgetModifier = ColdWidthModifier(id = 0))
    assertEquals(expected, actual)
  }

  @Test
  fun deleteWidgetModifier_emptyWidgetModifiers() {
    val widgetData =
      WidgetData(
        appWidgetId = 1,
        layers = listOf(ColdTextLayer(id = 0, widgetModifiers = emptyList())),
      )
    val expected =
      WidgetData(
        appWidgetId = 1,
        layers = listOf(ColdTextLayer(id = 0, widgetModifiers = listOf())),
      )
    val actual =
      widgetData.deleteWidgetModifier(layerId = 0, widgetModifier = ColdWidthModifier(id = 0))
    assertEquals(expected, actual)
  }

  @Test
  fun deleteWidgetModifier_test1() {
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
              widgetModifiers = listOf(ColdWidthModifier(id = 0), ColdWidthModifier(id = 2)),
            )
          ),
      )
    val actual =
      widgetData.deleteWidgetModifier(layerId = 0, widgetModifier = ColdWidthModifier(id = 1))
    assertEquals(expected, actual)
  }
}

package io.github.sadellie.sukko.core.model

import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.sadellie.sukko.core.model.modifier.ColdFillMaxWidthModifier
import io.github.sadellie.sukko.core.model.modifier.ColdWidthModifier
import kotlin.test.Test
import kotlin.test.assertEquals

class WidgetDataAddWidgetModifierTest {
  @Test
  fun addWidgetModifier_emptyLayers() {
    val widgetData = WidgetData(appWidgetId = 1)
    val expected = WidgetData(appWidgetId = 1)
    val actual =
      widgetData.addWidgetModifier(layerId = 1, widgetModifier = ColdWidthModifier(id = 0))
    assertEquals(expected, actual)
  }

  @Test
  fun addWidgetModifier_emptyWidgetModifiers() {
    val widgetData =
      WidgetData(
        appWidgetId = 1,
        layers = listOf(ColdTextLayer(id = 0, widgetModifiers = emptyList())),
      )
    val expected =
      WidgetData(
        appWidgetId = 1,
        layers = listOf(ColdTextLayer(id = 0, widgetModifiers = listOf(ColdWidthModifier(id = 0)))),
      )
    val actual =
      widgetData.addWidgetModifier(layerId = 0, widgetModifier = ColdWidthModifier(id = 0))
    assertEquals(expected, actual)
  }

  @Test
  fun addWidgetModifier_test1() {
    val widgetData =
      WidgetData(
        appWidgetId = 1,
        layers = listOf(ColdTextLayer(id = 0, widgetModifiers = listOf(ColdWidthModifier(id = 0)))),
      )
    val expected =
      WidgetData(
        appWidgetId = 1,
        layers =
          listOf(
            ColdTextLayer(
              id = 0,
              widgetModifiers = listOf(ColdWidthModifier(id = 0), ColdFillMaxWidthModifier(id = 1)),
            )
          ),
      )
    val actual =
      widgetData.addWidgetModifier(layerId = 0, widgetModifier = ColdFillMaxWidthModifier(id = 1))
    assertEquals(expected, actual)
  }
}

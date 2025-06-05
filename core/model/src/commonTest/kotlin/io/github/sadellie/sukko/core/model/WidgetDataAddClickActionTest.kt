package io.github.sadellie.sukko.core.model

import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import kotlin.test.Test
import kotlin.test.assertEquals

class WidgetDataAddClickActionTest {
  @Test
  fun addClickAction_emptyLayers() {
    val widgetData = WidgetData(appWidgetId = 1)
    val expected = WidgetData(appWidgetId = 1)
    val actual =
      widgetData.addClickAction(layerId = 1, clickAction = ClickAction.Cold.OpenLink(id = 0))
    assertEquals(expected, actual)
  }

  @Test
  fun addClickAction_emptyClickActions() {
    val widgetData =
      WidgetData(
        appWidgetId = 1,
        layers = listOf(ColdTextLayer(id = 0, clickActions = emptyList())),
      )
    val expected =
      WidgetData(
        appWidgetId = 1,
        layers =
          listOf(ColdTextLayer(id = 0, clickActions = listOf(ClickAction.Cold.OpenLink(id = 0)))),
      )
    val actual =
      widgetData.addClickAction(layerId = 0, clickAction = ClickAction.Cold.OpenLink(id = 0))
    assertEquals(expected, actual)
  }

  @Test
  fun addClickAction_test1() {
    val widgetData =
      WidgetData(
        appWidgetId = 1,
        layers =
          listOf(ColdTextLayer(id = 0, clickActions = listOf(ClickAction.Cold.OpenLink(id = 0)))),
      )
    val expected =
      WidgetData(
        appWidgetId = 1,
        layers =
          listOf(
            ColdTextLayer(
              id = 0,
              clickActions =
                listOf(ClickAction.Cold.OpenLink(id = 0), ClickAction.Cold.OpenLink(id = 1)),
            )
          ),
      )
    val actual =
      widgetData.addClickAction(layerId = 0, clickAction = ClickAction.Cold.OpenLink(id = 1))
    assertEquals(expected, actual)
  }
}

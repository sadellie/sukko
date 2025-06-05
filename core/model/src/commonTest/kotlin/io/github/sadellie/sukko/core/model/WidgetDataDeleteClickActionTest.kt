package io.github.sadellie.sukko.core.model

import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import kotlin.test.Test
import kotlin.test.assertEquals

class WidgetDataDeleteClickActionTest {
  @Test
  fun deleteClickAction_emptyLayers() {
    val widgetData = WidgetData(appWidgetId = 1)
    val expected = WidgetData(appWidgetId = 1)
    val actual =
      widgetData.deleteClickAction(layerId = 1, clickAction = ClickAction.Cold.OpenLink(id = 0))
    assertEquals(expected, actual)
  }

  @Test
  fun deleteClickAction_emptyClickActions() {
    val widgetData =
      WidgetData(
        appWidgetId = 1,
        layers = listOf(ColdTextLayer(id = 0, clickActions = emptyList())),
      )
    val expected =
      WidgetData(appWidgetId = 1, layers = listOf(ColdTextLayer(id = 0, clickActions = listOf())))
    val actual =
      widgetData.deleteClickAction(layerId = 0, clickAction = ClickAction.Cold.OpenLink(id = 0))
    assertEquals(expected, actual)
  }

  @Test
  fun deleteClickAction_test1() {
    val widgetData =
      WidgetData(
        appWidgetId = 1,
        layers =
          listOf(
            ColdTextLayer(
              id = 0,
              clickActions =
                listOf(
                  ClickAction.Cold.OpenLink(id = 0),
                  ClickAction.Cold.OpenLink(id = 1),
                  ClickAction.Cold.OpenLink(id = 2),
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
              clickActions =
                listOf(ClickAction.Cold.OpenLink(id = 0), ClickAction.Cold.OpenLink(id = 2)),
            )
          ),
      )
    val actual =
      widgetData.deleteClickAction(layerId = 0, clickAction = ClickAction.Cold.OpenLink(id = 1))
    assertEquals(expected, actual)
  }
}

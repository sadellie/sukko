package io.github.sadellie.sukko.feature.editor

import io.github.sadellie.sukko.core.model.WidgetData
import io.github.sadellie.sukko.core.model.WidgetSubscriptionInfo
import io.github.sadellie.sukko.core.model.basic.BrushSource
import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.basic.ImageUriSource
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.model.layer.ColdImageLayer
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.sadellie.sukko.core.model.modifier.ColdBackgroundColorModifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class GenerateWidgetSubscriptionInfoTest {
  @Test
  fun generateWidgetSubscriptionInfo_testEmptyLayers() = runTest {
    val expected = WidgetSubscriptionInfo(isTime = false, isBattery = false, isMedia = false)
    val widgetData = WidgetData(appWidgetId = 0, layers = emptyList())
    val actual = generateWidgetSubscriptionInfo(widgetData)

    assertEquals(expected, actual)
  }

  @Test
  fun generateWidgetSubscriptionInfo_testIsTimeWithModifier() = runTest {
    val expected = WidgetSubscriptionInfo(isTime = true, isBattery = false, isMedia = false)
    val widgetData =
      WidgetData(
        appWidgetId = 0,
        layers =
          listOf(
            ColdTextLayer(
              id = 0,
              widgetModifiers =
                listOf(
                  ColdBackgroundColorModifier(
                    id = 0,
                    color =
                      BrushSource.SolidColor(ScriptableColor.Script("""currentDate("format")""")),
                  )
                ),
            )
          ),
      )
    val actual = generateWidgetSubscriptionInfo(widgetData)

    assertEquals(expected, actual)
  }

  @Test
  fun generateWidgetSubscriptionInfo_testIsMediaWithActions() = runTest {
    val expected = WidgetSubscriptionInfo(isTime = false, isBattery = false, isMedia = true)
    val widgetData =
      WidgetData(
        appWidgetId = 0,
        layers =
          listOf(
            ColdTextLayer(
              id = 0,
              clickActions = listOf<ClickAction.Cold>(ClickAction.MediaPause(id = 0)),
            )
          ),
      )
    val actual = generateWidgetSubscriptionInfo(widgetData)

    assertEquals(expected, actual)
  }

  @Test
  fun generateWidgetSubscriptionInfo_testIsMediaWithImageSource() = runTest {
    val expected = WidgetSubscriptionInfo(isTime = false, isBattery = false, isMedia = true)
    val widgetData =
      WidgetData(
        appWidgetId = 0,
        layers = listOf(ColdImageLayer(id = 0, imageUriSource = ImageUriSource.AlbumCover)),
      )
    val actual = generateWidgetSubscriptionInfo(widgetData)

    assertEquals(expected, actual)
  }
}

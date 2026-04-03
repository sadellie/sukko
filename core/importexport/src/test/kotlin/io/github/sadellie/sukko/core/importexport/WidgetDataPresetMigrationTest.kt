package io.github.sadellie.sukko.core.importexport

import io.github.sadellie.sukko.core.iconfiles.IconFile
import io.github.sadellie.sukko.core.iconfiles.IconPack
import io.github.sadellie.sukko.core.model.Globals
import io.github.sadellie.sukko.core.model.WidgetDataPreset
import io.github.sadellie.sukko.core.model.basic.ImageUriSource
import io.github.sadellie.sukko.core.model.layer.ColdImageLayer
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals

class WidgetDataPresetMigrationTest {
  @Test
  fun migrate1_1() {
    val inputJson =
      """
      {
      	"presetId": 123,
      	"name": "Preset 1",
      	"layers": [
      		{
      			"type": "io.github.sadellie.sukko.core.model.layer.ColdTextLayer",
      			"id": 0,
      			"name": "text layer 1"
      		},
      		{
      			"type": "io.github.sadellie.sukko.core.model.layer.ColdImageLayer",
      			"id": 1,
      			"name": "icon layer 1",
      			"imageUriSource": {
      				"type": "io.github.sadellie.sukko.core.model.basic.ImageUriSource.IconPack"
      			}
      		},
      		{
      			"type": "io.github.sadellie.sukko.core.model.layer.ColdTextLayer",
      			"id": 2
      		},
      		{
      			"type": "io.github.sadellie.sukko.core.model.layer.ColdImageLayer",
      			"id": 4,
      			"name": "icon layer 2",
      			"imageUriSource": {
      				"type": "io.github.sadellie.sukko.core.model.basic.ImageUriSource.IconPack",
      				"iconFile": {
      					"fileName": "test.svg",
      					"iconPack": {
      						"type": "io.github.sadellie.sukko.core.iconfiles.IconPack.Custom",
      						"iconPackId": 7,
      						"name": "Icon pack 7"
      					}
      				}
      			}
      		}
      	],
      	"globals": {}
      }
      """
        .trimIndent()

    val expected =
      WidgetDataPreset.Custom(
        presetId = 123,
        name = "Preset 1",
        layers =
          listOf(
            ColdTextLayer(id = 0, name = "text layer 1"),
            ColdImageLayer(
              id = 1,
              parentId = null,
              name = "icon layer 1",
              imageUriSource = ImageUriSource.IconPack(iconFile = null),
            ),
            ColdTextLayer(id = 2),
            ColdImageLayer(
              id = 4,
              parentId = null,
              name = "icon layer 2",
              imageUriSource =
                ImageUriSource.IconPack(
                  iconFile =
                    IconFile(
                      fileName = "test.svg",
                      iconPack = IconPack.Custom(iconPackId = 7, name = "Icon pack 7"),
                    )
                ),
            ),
          ),
        globals = Globals(),
      )
    val expectedJson = Json.encodeToString(expected)
    val actual =
      try {
        consumeAndMigrateWidgetDataPreset(inputJson, 1)
      } catch (e: Exception) {
        println(expectedJson)
        error(e)
      }
    assertEquals(expected, actual, Json.encodeToString(expected))
  }
}

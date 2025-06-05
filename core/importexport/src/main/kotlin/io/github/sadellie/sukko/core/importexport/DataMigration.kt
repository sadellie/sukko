package io.github.sadellie.sukko.core.importexport

import io.github.sadellie.sukko.core.common.SCHEMA_VERSION
import io.github.sadellie.sukko.core.model.WidgetDataPreset
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

fun consumeAndMigrateWidgetDataPreset(widgetDataPreset: String, from: Int): WidgetDataPreset.Custom {
  if (from > SCHEMA_VERSION) error("Unsupported schema version: $from, max is $SCHEMA_VERSION")
  if (from == SCHEMA_VERSION) return Json.decodeFromString(widgetDataPreset)
  var json = Json.parseToJsonElement(widgetDataPreset)
  //  var currentSchemaVersion = from
  //  if (currentSchemaVersion == 2) {
  //    // migrate to 3
  //    val result = json.jsonObject.toMutableMap()
  //    result.remove("fullPreviewPath")
  //    json = JsonObject(result)
  //    // set to chain migration steps
  //    currentSchemaVersion = 3
  //  }

  val widgetDataPreset = Json.decodeFromJsonElement<WidgetDataPreset.Custom>(json)

  return widgetDataPreset
}

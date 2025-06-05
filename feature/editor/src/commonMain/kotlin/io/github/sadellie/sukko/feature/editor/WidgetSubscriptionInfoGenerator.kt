package io.github.sadellie.sukko.feature.editor

import io.github.sadellie.sukko.core.model.WidgetData
import io.github.sadellie.sukko.core.model.WidgetSubscriptionInfo
import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.basic.ImageUriSource
import io.github.sadellie.sukko.core.model.layer.ColdImageLayer
import io.github.sadellie.sukko.core.model.layer.Layer
import io.github.sadellie.sukko.core.script.token.Token3
import io.github.sadellie.sukko.core.script.token.tokenize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement

suspend fun generateWidgetSubscriptionInfo(widgetData: WidgetData): WidgetSubscriptionInfo =
  withContext(Dispatchers.Default) {
    if (widgetData.layers.isEmpty()) {
      return@withContext WidgetSubscriptionInfo(isTime = false, isBattery = false, isMedia = false)
    }
    var isTime = false
    var isBattery = false
    var isMedia = false

    for (layer in widgetData.layers) {
      // do not check if already known
      val allScriptTokens = layer.getAllScriptTokens()
      if (!isTime) isTime = isTimeSubscriber(allScriptTokens)
      if (!isBattery) isBattery = isBatterySubscriber(allScriptTokens)
      if (!isMedia) isMedia = isMediaSubscriber(layer, allScriptTokens)

      // stop checking, widget will subscribe to everything
      if (isTime && isBattery && isMedia) break
    }

    return@withContext WidgetSubscriptionInfo(
      isTime = isTime,
      isBattery = isBattery,
      isMedia = isMedia,
    )
  }

private fun isTimeSubscriber(allScriptTokens: Set<Token3>): Boolean {
  val timeTokens =
    setOf(
      Token3.Function.CurrentDate,
      Token3.Function.CurrentDateWithTimeZone,
      Token3.Const.CurrentTimestamp,
    )
  val hasTimeTokens = timeTokens.any { it in allScriptTokens }

  return hasTimeTokens
}

private fun isBatterySubscriber(allScriptTokens: Set<Token3>): Boolean {
  val batteryTokens =
    setOf(Token3.Const.BatteryLevel, Token3.Const.BatteryStatus, Token3.Const.BatteryFullEmpty)
  val hasBatteryTokens = batteryTokens.any { it in allScriptTokens }

  return hasBatteryTokens
}

private fun isMediaSubscriber(layer: Layer.Cold, allScriptTokens: Set<Token3>): Boolean {
  if (layer is ColdImageLayer) {
    if (layer.imageUriSource is ImageUriSource.AlbumCover) return true
    if (layer.imageUriSource is ImageUriSource.PlayerIcon) return true
  }
  if (layer.clickActions.any { it is ClickAction.MediaOpenPlayer }) return true
  val mediaTokens =
    setOf(
      Token3.Const.MediaTitle,
      Token3.Const.MediaDuration,
      Token3.Const.MediaPosition,
      Token3.Const.MediaArtist,
      Token3.Const.MediaCover,
      Token3.Const.PlayerName,
      Token3.Const.PlayerIcon,
      Token3.Const.PlayerState,
    )
  val hasMediaTokens = mediaTokens.any { it in allScriptTokens }
  if (hasMediaTokens) return true
  val hasMediaActions =
    layer.clickActions.any { clickAction -> clickAction is ClickAction.MediaAction }

  return hasMediaActions
}

private fun Layer.Cold.getAllScriptTokens(): Set<Token3> {
  val mapped = Json.encodeToJsonElement(this)
  val allScriptValues =
    findValuesByKey(mapped, "script")
      .filterIsInstance<JsonPrimitive>()
      .map { tokenize(it.content) }
      .flatten()
      .toSet()
  return allScriptValues
}

private fun findValuesByKey(jsonElement: JsonElement, targetKey: String): List<JsonElement> {
  val results = mutableListOf<JsonElement>()

  fun traverse(element: JsonElement) {
    when (element) {
      is JsonObject -> {
        element[targetKey]?.let { results.add(it) }
        element.values.forEach { traverse(it) }
      }
      is JsonArray -> element.forEach { traverse(it) }
      else -> Unit
    }
  }

  traverse(jsonElement)
  return results
}

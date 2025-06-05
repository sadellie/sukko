package io.github.sadellie.sukko.core.model

import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.basic.GlobalValue
import io.github.sadellie.sukko.core.model.layer.Layer
import io.github.sadellie.sukko.core.model.modifier.WidgetModifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import okio.Path

/** Data for widget with [layers] and canvas information. This object is shared between users. */
@Serializable
data class WidgetData(
  val appWidgetId: Int,
  val name: String? = null,
  val layers: List<Layer.Cold> = emptyList(),
  val globals: Globals = Globals(),
) {
  companion object {
    private const val DIR_PATH = "widgetData"
    private const val PREVIEW_IMAGE_NAME = "preview.png"
  }

  fun getDataPath(filesDirPath: Path) = filesDirPath / DIR_PATH / appWidgetId.toString()

  fun getPreviewPath(filesDirPath: Path) = getDataPath(filesDirPath) / PREVIEW_IMAGE_NAME

  /** [layer]'s [Layer.id] can be 0 - valid id will be calculated before adding this new layer. */
  fun addLayer(layer: Layer.Cold): WidgetData {
    val allIds = this.layers.map { it.id }
    // always allow adding to root and empty layers list
    val parentIdCheckRequired = layer.parentId != null && this.layers.isNotEmpty()
    // do not add if parent id is not valid
    if (parentIdCheckRequired && layer.parentId !in allIds) return this

    val newId = allIds.maxOrNull()?.plus(1) ?: 0
    val layerToAdd = layer.updateId(newId)
    val newLayers = this.layers + layerToAdd
    return this.copy(layers = newLayers)
  }

  fun deleteLayer(id: Int): WidgetData {
    val idsToDelete = mutableSetOf(id)
    val idsToVisit = mutableSetOf(id)
    while (idsToVisit.isNotEmpty()) {
      val currentId = idsToVisit.last()
      val childrenLayerIds = this.layers.filter { it.parentId == currentId }.map { it.id }
      idsToDelete.addAll(childrenLayerIds)
      idsToVisit.addAll(childrenLayerIds)
      idsToVisit.remove(currentId)
    }

    val newLayers = this.layers.filter { it.id !in idsToDelete }
    return this.copy(layers = newLayers)
  }

  fun updateLayer(updatedLayer: Layer.Cold): WidgetData {
    val indexOfLayerToUpdate = this.layers.indexOfFirst { layer -> layer.id == updatedLayer.id }
    if (indexOfLayerToUpdate == -1) return this
    val updatedLayers = this.layers.toMutableList()
    updatedLayers[indexOfLayerToUpdate] = updatedLayer
    return this.copy(layers = updatedLayers)
  }

  fun updateLayerOrder(updatedLayers: List<Layer.Cold>): WidgetData {
    if (this.layers.isEmpty()) return this
    if (updatedLayers.isEmpty()) return this
    // remove old sorted layers and read them but with new order
    val updatedLayers = this.layers.minus(updatedLayers.toSet()).plus(updatedLayers)
    return this.copy(layers = updatedLayers)
  }

  /** Updates [WidgetModifier.id] internally, pass 0 or whatever */
  fun addWidgetModifier(layerId: Int, widgetModifier: WidgetModifier.Cold): WidgetData {
    val layer = getLayerById(layerId) ?: return this
    val maxId = layer.widgetModifiers.maxOfOrNull { it.id } ?: -1
    val newId = maxId + 1
    val updatedWidgetModifier = widgetModifier.updateId(newId)
    val newWidgetModifiers = layer.widgetModifiers + updatedWidgetModifier
    val updatedData = updateWidgetModifiers(layer, newWidgetModifiers)
    return updatedData
  }

  fun updateWidgetModifier(layerId: Int, widgetModifier: WidgetModifier.Cold): WidgetData {
    val layer = getLayerById(layerId) ?: return this
    val widgetModifierIndex = layer.widgetModifiers.indexOfFirst { it.id == widgetModifier.id }
    if (widgetModifierIndex == -1) {
      Logger.e(TAG) { "updateWidgetModifier: $widgetModifier not found" }
      return this
    }
    val newWidgetModifiers = layer.widgetModifiers.toMutableList()
    newWidgetModifiers[widgetModifierIndex] = widgetModifier
    val updatedData = updateWidgetModifiers(layer, newWidgetModifiers)
    return updatedData
  }

  fun deleteWidgetModifier(layerId: Int, widgetModifier: WidgetModifier.Cold): WidgetData {
    val layer = getLayerById(layerId) ?: return this
    val newWidgetModifiers = layer.widgetModifiers.filter { it.id != widgetModifier.id }
    val updatedData = updateWidgetModifiers(layer, newWidgetModifiers)
    return updatedData
  }

  fun reorderWidgetModifier(layerId: Int, updatedModifiers: List<WidgetModifier.Cold>): WidgetData {
    if (updatedModifiers.isEmpty()) return this
    val layer = getLayerById(layerId) ?: return this
    return updateWidgetModifiers(layer, updatedModifiers)
  }

  fun addClickAction(layerId: Int, clickAction: ClickAction.Cold): WidgetData {
    val layer = getLayerById(layerId) ?: return this
    val maxId = layer.clickActions.maxOfOrNull { it.id } ?: -1
    val newId = maxId + 1
    val updatedClickAction = clickAction.updateId(newId)
    val newClickActions = layer.clickActions + updatedClickAction
    val updatedData = updateClickActions(layer, newClickActions)
    return updatedData
  }

  fun updateClickAction(layerId: Int, clickAction: ClickAction.Cold): WidgetData {
    val layer = getLayerById(layerId) ?: return this
    val clickActionIndex = layer.clickActions.indexOfFirst { it.id == clickAction.id }
    if (clickActionIndex == -1) {
      Logger.e(TAG) { "updateClickAction: $clickAction not found" }
      return this
    }
    val newClickActions = layer.clickActions.toMutableList()
    newClickActions[clickActionIndex] = clickAction
    val updatedData = updateClickActions(layer, newClickActions)
    return updatedData
  }

  fun deleteClickAction(layerId: Int, clickAction: ClickAction.Cold): WidgetData {
    val layer = getLayerById(layerId) ?: return this
    val newClickActions = layer.clickActions.filter { it.id != clickAction.id }
    val updatedData = updateClickActions(layer, newClickActions)
    return updatedData
  }

  fun reorderClickActions(layerId: Int, updatedClickActions: List<ClickAction.Cold>): WidgetData {
    if (updatedClickActions.isEmpty()) return this
    val layer = getLayerById(layerId) ?: return this
    return updateClickActions(layer, updatedClickActions)
  }

  private fun getLayerById(layerId: Int): Layer.Cold? {
    val layer = this.layers.firstOrNull { it.id == layerId }
    if (layer == null) Logger.e(TAG) { "getLayerById: $layerId not found" }
    return layer
  }

  /** Updates widget modifiers for a given layer */
  private fun updateWidgetModifiers(
    layer: Layer.Cold,
    newWidgetModifiers: List<WidgetModifier.Cold>,
  ): WidgetData {
    val layerIndex = this.layers.indexOfFirst { it.id == layer.id }
    if (layerIndex == -1) {
      Logger.e(TAG) { "updateWidgetModifiers: $layer not found" }
      return this
    }
    val updateLayers = this.layers.toMutableList()
    val updatedLayer = layer.updateWidgetModifiers(newWidgetModifiers)
    updateLayers[layerIndex] = updatedLayer
    return this.copy(layers = updateLayers)
  }

  private fun updateClickActions(
    layer: Layer.Cold,
    newClickActions: List<ClickAction.Cold>,
  ): WidgetData {
    val layerIndex = this.layers.indexOfFirst { it.id == layer.id }
    if (layerIndex == -1) {
      Logger.e(TAG) { "updateClickActions: $layer not found" }
      return this
    }
    val updateLayers = this.layers.toMutableList()
    val updatedLayer = layer.updateClickActions(newClickActions)
    updateLayers[layerIndex] = updatedLayer
    return this.copy(layers = updateLayers)
  }
}

@Serializable
data class Globals(
  val strings: List<GlobalValue.GlobalString> = emptyList(),
  val booleans: List<GlobalValue.GlobalBoolean> = emptyList(),
  val doubles: List<GlobalValue.GlobalDouble> = emptyList(),
  val dps: List<GlobalValue.GlobalDp> = emptyList(),
  val sps: List<GlobalValue.GlobalSp> = emptyList(),
  val colors: List<GlobalValue.GlobalColor> = emptyList(),
  val textStyles: List<GlobalValue.GlobalTextStyle> = emptyList(),
) {
  fun isEmpty() =
    (strings.size +
      booleans.size +
      doubles.size +
      dps.size +
      sps.size +
      colors.size +
      textStyles.size) == 0

  suspend fun findString(id: Long): GlobalValue.GlobalString? = findInList(strings, id)

  suspend fun findBoolean(id: Long): GlobalValue.GlobalBoolean? = findInList(booleans, id)

  suspend fun findDouble(id: Long): GlobalValue.GlobalDouble? = findInList(doubles, id)

  suspend fun findDp(id: Long): GlobalValue.GlobalDp? = findInList(dps, id)

  suspend fun findSp(id: Long): GlobalValue.GlobalSp? = findInList(sps, id)

  suspend fun findColor(id: Long): GlobalValue.GlobalColor? = findInList(colors, id)

  suspend fun findTextStyle(id: Long): GlobalValue.GlobalTextStyle? = findInList(textStyles, id)

  fun addGlobal(newGlobal: GlobalValue<*>) =
    when (newGlobal) {
      is GlobalValue.GlobalString ->
        this.copy(strings = strings + newGlobal.copy(id = generateNewId(strings)))
      is GlobalValue.GlobalBoolean ->
        this.copy(booleans = booleans + newGlobal.copy(id = generateNewId(booleans)))
      is GlobalValue.GlobalDouble ->
        this.copy(doubles = doubles + newGlobal.copy(id = generateNewId(doubles)))
      is GlobalValue.GlobalDp -> this.copy(dps = dps + newGlobal.copy(id = generateNewId(dps)))
      is GlobalValue.GlobalSp -> this.copy(sps = sps + newGlobal.copy(id = generateNewId(sps)))
      is GlobalValue.GlobalColor ->
        this.copy(colors = colors + newGlobal.copy(id = generateNewId(colors)))
      is GlobalValue.GlobalTextStyle ->
        this.copy(textStyles = textStyles + newGlobal.copy(id = generateNewId(textStyles)))
    }

  fun deleteGlobal(globalToDelete: GlobalValue<*>) =
    when (globalToDelete) {
      is GlobalValue.GlobalString ->
        this.copy(strings = strings.filter { it.id != globalToDelete.id })
      is GlobalValue.GlobalBoolean ->
        this.copy(booleans = booleans.filter { it.id != globalToDelete.id })
      is GlobalValue.GlobalDouble ->
        this.copy(doubles = doubles.filter { it.id != globalToDelete.id })
      is GlobalValue.GlobalDp -> this.copy(dps = dps.filter { it.id != globalToDelete.id })
      is GlobalValue.GlobalSp -> this.copy(sps = sps.filter { it.id != globalToDelete.id })
      is GlobalValue.GlobalColor -> this.copy(colors = colors.filter { it.id != globalToDelete.id })
      is GlobalValue.GlobalTextStyle ->
        this.copy(textStyles = textStyles.filter { it.id != globalToDelete.id })
    }

  fun updateGlobal(globalToUpdate: GlobalValue<*>) =
    when (globalToUpdate) {
      is GlobalValue.GlobalString ->
        this.copy(strings = updateGlobalInList(strings, globalToUpdate))
      is GlobalValue.GlobalBoolean ->
        this.copy(booleans = updateGlobalInList(booleans, globalToUpdate))
      is GlobalValue.GlobalDouble ->
        this.copy(doubles = updateGlobalInList(doubles, globalToUpdate))
      is GlobalValue.GlobalDp -> this.copy(dps = updateGlobalInList(dps, globalToUpdate))
      is GlobalValue.GlobalSp -> this.copy(sps = updateGlobalInList(sps, globalToUpdate))
      is GlobalValue.GlobalColor -> this.copy(colors = updateGlobalInList(colors, globalToUpdate))
      is GlobalValue.GlobalTextStyle ->
        this.copy(textStyles = updateGlobalInList(textStyles, globalToUpdate))
    }

  private suspend fun <T : GlobalValue<*>> findInList(listOfGlobal: List<T>, id: Long): T? =
    withContext(Dispatchers.Default) { listOfGlobal.firstOrNull { it.id == id } }

  private fun <T : GlobalValue<*>> updateGlobalInList(
    listOfGlobal: List<T>,
    globalToUpdate: T,
  ): List<T> {
    val updatedGlobals = listOfGlobal.toMutableList()
    val indexToUpdate = updatedGlobals.indexOfFirst { it.id == globalToUpdate.id }
    if (indexToUpdate == -1) {
      Logger.e(TAG) { "updateGlobal: $globalToUpdate not found" }
      return listOfGlobal
    }
    updatedGlobals[indexToUpdate] = globalToUpdate
    return updatedGlobals
  }

  private fun <T : GlobalValue<*>> generateNewId(listOfGlobal: List<T>): Long {
    val maxId = listOfGlobal.maxOfOrNull { it.id }
    return if (maxId == null) 0L else maxId + 1L
  }
}

private const val TAG = "WidgetData"

package io.github.sadellie.sukko.core.model

import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.model.basic.GlobalValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
data class Globals(
  val strings: List<GlobalValue.GlobalString> = emptyList(),
  val booleans: List<GlobalValue.GlobalBoolean> = emptyList(),
  val doubles: List<GlobalValue.GlobalDouble> = emptyList(),
  val colors: List<GlobalValue.GlobalColor> = emptyList(),
  val textStyles: List<GlobalValue.GlobalTextStyle> = emptyList(),
) {
  fun isEmpty() = (strings.size + booleans.size + doubles.size + colors.size + textStyles.size) == 0

  suspend fun findString(id: Long): GlobalValue.GlobalString? = findInList(strings, id)

  suspend fun findBoolean(id: Long): GlobalValue.GlobalBoolean? = findInList(booleans, id)

  suspend fun findDouble(id: Long): GlobalValue.GlobalDouble? = findInList(doubles, id)

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
      is GlobalValue.GlobalColor -> this.copy(colors = colors.filter { it.id != globalToDelete.id })
      is GlobalValue.GlobalTextStyle ->
        this.copy(textStyles = textStyles.filter { it.id != globalToDelete.id })
    }

  /** Returns a copy with replaced global, will find it by [GlobalValue.id] AND type. */
  fun updateGlobal(globalToUpdate: GlobalValue<*>) =
    when (globalToUpdate) {
      is GlobalValue.GlobalString ->
        this.copy(strings = updateGlobalInList(strings, globalToUpdate))
      is GlobalValue.GlobalBoolean ->
        this.copy(booleans = updateGlobalInList(booleans, globalToUpdate))
      is GlobalValue.GlobalDouble ->
        this.copy(doubles = updateGlobalInList(doubles, globalToUpdate))
      is GlobalValue.GlobalColor -> this.copy(colors = updateGlobalInList(colors, globalToUpdate))
      is GlobalValue.GlobalTextStyle ->
        this.copy(textStyles = updateGlobalInList(textStyles, globalToUpdate))
    }

  private suspend fun <T : GlobalValue<*>> findInList(listOfGlobal: List<T>, id: Long): T? =
    withContext(Dispatchers.Default) {
      val value = listOfGlobal.firstOrNull { it.id == id }
      if (value == null) {
        Logger.e(tag = TAG) { "findInList: Failed to find global with id $id in $listOfGlobal" }
      }
      value
    }

  private fun <T : GlobalValue<*>> updateGlobalInList(
    listOfGlobal: List<T>,
    globalToUpdate: T,
  ): List<T> {
    val updatedGlobals = listOfGlobal.toMutableList()
    val indexToUpdate = updatedGlobals.indexOfFirst { it.id == globalToUpdate.id }
    if (indexToUpdate == -1) {
      Logger.e(tag = TAG) { "updateGlobal: $globalToUpdate not found" }
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

private const val TAG = "Globals"

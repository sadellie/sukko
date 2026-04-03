package io.github.sadellie.sukko.core.model.basic

import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_click_action_launch_app
import io.github.sadellie.sukko.resources.core_model_click_action_media_open_player
import io.github.sadellie.sukko.resources.core_model_click_action_media_pause
import io.github.sadellie.sukko.resources.core_model_click_action_media_play
import io.github.sadellie.sukko.resources.core_model_click_action_media_skip_to_next
import io.github.sadellie.sukko.resources.core_model_click_action_media_skip_to_previous
import io.github.sadellie.sukko.resources.core_model_click_action_open_link
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.StringResource

@Serializable
sealed interface ClickAction {
  val id: Int

  @Serializable
  sealed interface Cold : ClickAction {
    val displayName: StringResource

    fun updateId(newId: Int): Cold

    companion object {
      fun values(): List<Cold> =
        listOf(
          OpenLink(id = 0),
          LaunchApp(id = 0),
          MediaPause(id = 0),
          MediaPlay(id = 0),
          MediaSkipToNext(id = 0),
          MediaSkipToPrevious(id = 0),
          MediaOpenPlayer(id = 0),
        )
    }

    @Serializable
    data class OpenLink(
      override val id: Int,
      val url: ScriptableString = ScriptableString.Fixed("http://example.com/"),
    ) : Cold {
      @Transient override val displayName = Res.string.core_model_click_action_open_link

      override fun updateId(newId: Int) = this.copy(id = newId)
    }
  }

  @Serializable
  sealed interface Evaluated : ClickAction {
    @Serializable data class OpenLink(override val id: Int, val url: String) : Evaluated
  }

  @Serializable sealed interface MediaAction

  @Serializable
  data class LaunchApp(
    override val id: Int,
    val label: String? = null,
    val packageName: String? = null,
  ) : Cold, Evaluated {
    @Transient override val displayName = Res.string.core_model_click_action_launch_app

    override fun updateId(newId: Int) = this.copy(id = newId)
  }

  @Serializable
  data class MediaPause(override val id: Int) : Cold, Evaluated, MediaAction {
    @Transient override val displayName = Res.string.core_model_click_action_media_pause

    override fun updateId(newId: Int) = this.copy(id = newId)
  }

  @Serializable
  data class MediaPlay(override val id: Int) : Cold, Evaluated, MediaAction {
    @Transient override val displayName = Res.string.core_model_click_action_media_play

    override fun updateId(newId: Int) = this.copy(id = newId)
  }

  @Serializable
  data class MediaSkipToNext(override val id: Int) : Cold, Evaluated, MediaAction {
    @Transient override val displayName = Res.string.core_model_click_action_media_skip_to_next

    override fun updateId(newId: Int) = this.copy(id = newId)
  }

  @Serializable
  data class MediaSkipToPrevious(override val id: Int) : Cold, Evaluated, MediaAction {
    @Transient override val displayName = Res.string.core_model_click_action_media_skip_to_previous

    override fun updateId(newId: Int) = this.copy(id = newId)
  }

  @Serializable
  data class MediaOpenPlayer(override val id: Int) : Cold, Evaluated, MediaAction {
    @Transient override val displayName = Res.string.core_model_click_action_media_open_player

    override fun updateId(newId: Int) = this.copy(id = newId)
  }
}

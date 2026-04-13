package io.github.sadellie.sukko.core.data.script.simplify

import io.github.sadellie.sukko.core.data.ImageProvider
import io.github.sadellie.sukko.core.data.script.ASTNode
import io.github.sadellie.sukko.core.data.script.ConstantNode
import io.github.sadellie.sukko.core.data.script.NumberNode
import io.github.sadellie.sukko.core.data.script.ScriptContext
import io.github.sadellie.sukko.core.data.script.TextNode
import io.github.sadellie.sukko.core.data.script.token.Token3

internal val SimplifyConstant =
  object : SimplificationRule {
    override suspend fun simplify(context: ScriptContext, tree: ASTNode): SimplificationStep? =
      simplifyBottomToTop(SimplificationType.EVAL_CONSTANT, context, tree) walker@{ currentNode ->
        // only work with constant
        if (currentNode !is ConstantNode) return@walker null

        val evaluationResult =
          when (currentNode.token) {
            Token3.Const.BatteryLevel -> context.batteryInfoProvider.capacity
            Token3.Const.BatteryStatus -> context.batteryInfoProvider.status
            Token3.Const.BatteryFullEmpty -> context.batteryInfoProvider.chargeDischargeSeconds
            Token3.Const.MediaArtist -> context.mediaInfoProvider.artist
            Token3.Const.MediaTitle -> context.mediaInfoProvider.title
            Token3.Const.MediaDuration -> context.mediaInfoProvider.durationSeconds
            Token3.Const.MediaPosition -> context.mediaInfoProvider.positionSeconds
            Token3.Const.MediaCover -> ImageProvider.ALBUM_COVER_URI
            Token3.Const.PlayerName -> context.mediaInfoProvider.playerName
            Token3.Const.PlayerIcon -> ImageProvider.PLAYER_ICON_URI
            Token3.Const.PlayerState -> context.mediaInfoProvider.playerState
            Token3.Const.DeviceModel -> context.deviceInfoProvider.model
            Token3.Const.CurrentTimestamp -> context.dateTimeProvider.currentTimestamp
            Token3.Const.VolumeMusicMin -> context.mediaInfoProvider.volumeMusicMin
            Token3.Const.VolumeMusic -> context.mediaInfoProvider.volumeMusic
            Token3.Const.VolumeMusicMax -> context.mediaInfoProvider.volumeMusicMax
          }

        return@walker when (evaluationResult) {
          is String -> TextNode(evaluationResult)
          is Double -> NumberNode(evaluationResult)
          is Int -> NumberNode(evaluationResult)
          is Long -> NumberNode(evaluationResult.toDouble())
          is Float -> NumberNode(evaluationResult.toDouble())
          null -> TextNode("")
          else -> TextNode(evaluationResult.toString())
        }
      }
  }

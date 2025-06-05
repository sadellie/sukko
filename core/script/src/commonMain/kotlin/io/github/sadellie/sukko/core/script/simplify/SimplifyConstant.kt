package io.github.sadellie.sukko.core.script.simplify

import io.github.sadellie.sukko.core.script.ASTNode
import io.github.sadellie.sukko.core.script.ConstantNode
import io.github.sadellie.sukko.core.script.NumberNode
import io.github.sadellie.sukko.core.script.ScriptContext
import io.github.sadellie.sukko.core.script.TextNode
import io.github.sadellie.sukko.core.script.token.Token3

internal val SimplifyConstant =
  object : SimplificationRule {
    override suspend fun simplify(context: ScriptContext, tree: ASTNode): SimplificationStep? =
      simplifyBottomToTop(SimplificationType.EVAL_CONSTANT, context, tree) walker@{ currentNode ->
        // only work with constant
        if (currentNode !is ConstantNode) return@walker null

        val evaluationResult =
          when (currentNode.token) {
            Token3.Const.BatteryLevel -> context.batteryCapacity
            Token3.Const.BatteryStatus -> context.batteryStatus
            Token3.Const.BatteryFullEmpty -> context.batteryChargeDischargeSeconds
            Token3.Const.MediaArtist -> context.mediaArtist
            Token3.Const.MediaTitle -> context.mediaTitle
            Token3.Const.MediaDuration -> context.mediaDuration
            Token3.Const.MediaPosition -> context.mediaPosition
            Token3.Const.MediaCover -> context.mediaCoverUri
            Token3.Const.PlayerName -> context.playerName
            Token3.Const.PlayerIcon -> context.playerIcon
            Token3.Const.PlayerState -> context.playerState
            Token3.Const.DeviceModel -> context.deviceModel
            Token3.Const.CurrentTimestamp -> context.currentTimestamp
            Token3.Const.VolumeMusicMin -> context.volumeMusicMin
            Token3.Const.VolumeMusic -> context.volumeMusic
            Token3.Const.VolumeMusicMax -> context.volumeMusicMax
          }

        return@walker when (evaluationResult) {
          is String -> TextNode(evaluationResult)
          is Double -> NumberNode(evaluationResult)
          is Int -> NumberNode(evaluationResult)
          is Long -> NumberNode(evaluationResult)
          null -> TextNode("")
          else -> TextNode(evaluationResult.toString())
        }
      }
  }

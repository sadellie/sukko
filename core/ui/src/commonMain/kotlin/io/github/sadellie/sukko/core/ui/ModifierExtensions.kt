package io.github.sadellie.sukko.core.ui

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp

fun Modifier.squashable(
  onClick: () -> Unit = {},
  onLongClick: (() -> Unit)? = null,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource,
  cornerRadiusRange: IntRange,
  role: Role = Role.Button,
  animationSpec: AnimationSpec<Int> = tween(),
) = composed {
  val isPressed by interactionSource.collectIsPressedAsState()
  val cornerRadius: Int by
    animateIntAsState(
      targetValue = if (isPressed) cornerRadiusRange.first else cornerRadiusRange.last,
      animationSpec = animationSpec,
      label = "Squashed animation",
    )

  this.clip(RoundedCornerShape(cornerRadius))
    .combinedClickable(
      onClick = onClick,
      onLongClick = onLongClick,
      interactionSource = interactionSource,
      indication = ripple(),
      role = role,
      enabled = enabled,
    )
}

fun Modifier.squashable(
  onClick: () -> Unit = {},
  onLongClick: (() -> Unit)? = null,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource,
  cornerRadiusRange: ClosedRange<Dp>,
  role: Role = Role.Button,
  animationSpec: AnimationSpec<Dp> = tween(),
) = composed {
  val isPressed by interactionSource.collectIsPressedAsState()
  val cornerRadius: Dp by
    animateDpAsState(
      targetValue = if (isPressed) cornerRadiusRange.start else cornerRadiusRange.endInclusive,
      animationSpec = animationSpec,
      label = "Squashed animation",
    )

  this.clip(RoundedCornerShape(cornerRadius))
    .combinedClickable(
      onClick = onClick,
      onLongClick = onLongClick,
      interactionSource = interactionSource,
      indication = ripple(),
      role = role,
      enabled = enabled,
    )
}

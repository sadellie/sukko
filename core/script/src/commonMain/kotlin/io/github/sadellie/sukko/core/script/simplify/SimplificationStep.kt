package io.github.sadellie.sukko.core.script.simplify

import io.github.sadellie.sukko.core.script.ASTNode

internal data class SimplificationStep(
  val simplifiedASTNode: ASTNode,
  val type: SimplificationType,
)

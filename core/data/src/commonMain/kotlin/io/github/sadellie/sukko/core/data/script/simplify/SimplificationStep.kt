package io.github.sadellie.sukko.core.data.script.simplify

import io.github.sadellie.sukko.core.data.script.ASTNode

internal data class SimplificationStep(val simplifiedASTNode: ASTNode, val type: SimplificationType)

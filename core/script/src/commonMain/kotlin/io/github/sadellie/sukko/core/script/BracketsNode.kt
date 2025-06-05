package io.github.sadellie.sukko.core.script

import io.github.sadellie.sukko.core.script.token.Token3

internal data class BracketsNode(override val children: List<ASTNode>) : ASTNode {
  override val token = Token3.Parentheses.Left

  constructor(child: ASTNode) : this(listOf(child))

  override fun withNewChildren(children: List<ASTNode>) = this.copy(children = children)

  override fun toFormattedString(): String {
    val child = children.first().toFormattedString()
    return "($child)"
  }

  override fun collapse(scriptContext: ScriptContext): ASTNode {
    val child = children.firstOrNull()
    // extract atomic children to remove unnecessary bracket
    if (child is AtomicNode) return child
    return super.collapse(scriptContext)
  }
}

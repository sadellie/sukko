package io.github.sadellie.sukko.core.script.docs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("docs")
data class Docs(
  val constants: List<DocsItem.Constant>,
  val methods: List<DocsItem.Method>,
  val templates: List<DocsItem.Template>,
)

@Serializable
sealed interface DocsItem {
  val name: TranslatableString
  val returnTypes: List<ScriptTypeTag>

  @Serializable
  @SerialName("constant")
  data class Constant(
    override val name: TranslatableString,
    override val returnTypes: List<ScriptTypeTag>,
    val description: TranslatableString,
    val api: String,
  ) : DocsItem

  @Serializable
  @SerialName("method")
  data class Method(
    override val name: TranslatableString,
    override val returnTypes: List<ScriptTypeTag>,
    val description: TranslatableString,
    val api: String,
    val params: List<MethodParam>,
  ) : DocsItem

  @Serializable
  @SerialName("template")
  data class Template(
    override val name: TranslatableString,
    override val returnTypes: List<ScriptTypeTag>,
    val script: String,
  ) : DocsItem
}

@Serializable
@SerialName("methodParam")
data class MethodParam(
  val name: String,
  val description: TranslatableString,
  val types: List<ScriptTypeTag>,
)

/** Map of language and text in this language */
typealias TranslatableString = Map<String, String>

@SerialName("type")
enum class ScriptTypeTag {
  TEXT,
  NUMBER,
  BOOL,
}

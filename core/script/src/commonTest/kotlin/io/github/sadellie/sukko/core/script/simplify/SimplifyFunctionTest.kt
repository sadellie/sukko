package io.github.sadellie.sukko.core.script.simplify

import io.github.sadellie.sukko.core.script.ASTNode
import io.github.sadellie.sukko.core.script.TextNode
import io.github.sadellie.sukko.core.script.assertSimplifySingleRule
import kotlin.test.Test

class SimplifyFunctionTest {
  @Test
  fun simplify_functionIf1() =
    assertSimplify("""if(true, "correct", "incorrect")""", TextNode("correct"))

  @Test
  fun simplify_functionIf2() =
    assertSimplify("""if(false, "correct", "incorrect")""", TextNode("incorrect"))

  @Test
  fun simplify_functionCurrentDate1() =
    assertSimplify("""currentDate("format")""", TextNode("23:59"))

  @Test
  fun simplify_functionCurrentDateWithTimeZone1() =
    assertSimplify("""currentDateWithTimeZone("format", "UTC")""", TextNode("format-UTC"))

  @Test
  fun simplify_functionDynamicColor() {
    // dynamicColor("PRIMARY") = "#FFFFFF" // some hex (test string in this case)
    assertSimplify("""dynamicColor("PRIMARY")""", TextNode("VALUE_PRIMARY"))
    assertSimplify("""dynamicColor("ON_PRIMARY")""", TextNode("VALUE_ON_PRIMARY"))
    assertSimplify("""dynamicColor("PRIMARY_CONTAINER")""", TextNode("VALUE_PRIMARY_CONTAINER"))
    assertSimplify(
      """dynamicColor("ON_PRIMARY_CONTAINER")""",
      TextNode("VALUE_ON_PRIMARY_CONTAINER"),
    )
    assertSimplify("""dynamicColor("INVERSE_PRIMARY")""", TextNode("VALUE_INVERSE_PRIMARY"))
    assertSimplify("""dynamicColor("SECONDARY")""", TextNode("VALUE_SECONDARY"))
    assertSimplify("""dynamicColor("ON_SECONDARY")""", TextNode("VALUE_ON_SECONDARY"))
    assertSimplify("""dynamicColor("SECONDARY_CONTAINER")""", TextNode("VALUE_SECONDARY_CONTAINER"))
    assertSimplify(
      """dynamicColor("ON_SECONDARY_CONTAINER")""",
      TextNode("VALUE_ON_SECONDARY_CONTAINER"),
    )
    assertSimplify("""dynamicColor("TERTIARY")""", TextNode("VALUE_TERTIARY"))
    assertSimplify("""dynamicColor("ON_TERTIARY")""", TextNode("VALUE_ON_TERTIARY"))
    assertSimplify("""dynamicColor("TERTIARY_CONTAINER")""", TextNode("VALUE_TERTIARY_CONTAINER"))
    assertSimplify(
      """dynamicColor("ON_TERTIARY_CONTAINER")""",
      TextNode("VALUE_ON_TERTIARY_CONTAINER"),
    )
    assertSimplify("""dynamicColor("BACKGROUND")""", TextNode("VALUE_BACKGROUND"))
    assertSimplify("""dynamicColor("ON_BACKGROUND")""", TextNode("VALUE_ON_BACKGROUND"))
    assertSimplify("""dynamicColor("SURFACE")""", TextNode("VALUE_SURFACE"))
    assertSimplify("""dynamicColor("ON_SURFACE")""", TextNode("VALUE_ON_SURFACE"))
    assertSimplify("""dynamicColor("SURFACE_VARIANT")""", TextNode("VALUE_SURFACE_VARIANT"))
    assertSimplify("""dynamicColor("ON_SURFACE_VARIANT")""", TextNode("VALUE_ON_SURFACE_VARIANT"))
    assertSimplify("""dynamicColor("SURFACE_TINT")""", TextNode("VALUE_SURFACE_TINT"))
    assertSimplify("""dynamicColor("INVERSE_SURFACE")""", TextNode("VALUE_INVERSE_SURFACE"))
    assertSimplify("""dynamicColor("INVERSE_ON_SURFACE")""", TextNode("VALUE_INVERSE_ON_SURFACE"))
    assertSimplify("""dynamicColor("ERROR")""", TextNode("VALUE_ERROR"))
    assertSimplify("""dynamicColor("ON_ERROR")""", TextNode("VALUE_ON_ERROR"))
    assertSimplify("""dynamicColor("ERROR_CONTAINER")""", TextNode("VALUE_ERROR_CONTAINER"))
    assertSimplify("""dynamicColor("ON_ERROR_CONTAINER")""", TextNode("VALUE_ON_ERROR_CONTAINER"))
    assertSimplify("""dynamicColor("OUTLINE")""", TextNode("VALUE_OUTLINE"))
    assertSimplify("""dynamicColor("OUTLINE_VARIANT")""", TextNode("VALUE_OUTLINE_VARIANT"))
    assertSimplify("""dynamicColor("SCRIM")""", TextNode("VALUE_SCRIM"))
    assertSimplify("""dynamicColor("SURFACE_BRIGHT")""", TextNode("VALUE_SURFACE_BRIGHT"))
    assertSimplify("""dynamicColor("SURFACE_DIM")""", TextNode("VALUE_SURFACE_DIM"))
    assertSimplify("""dynamicColor("SURFACE_CONTAINER")""", TextNode("VALUE_SURFACE_CONTAINER"))
    assertSimplify(
      """dynamicColor("SURFACE_CONTAINER_HIGH")""",
      TextNode("VALUE_SURFACE_CONTAINER_HIGH"),
    )
    assertSimplify(
      """dynamicColor("SURFACE_CONTAINER_HIGHEST")""",
      TextNode("VALUE_SURFACE_CONTAINER_HIGHEST"),
    )
    assertSimplify(
      """dynamicColor("SURFACE_CONTAINER_LOW")""",
      TextNode("VALUE_SURFACE_CONTAINER_LOW"),
    )
    assertSimplify(
      """dynamicColor("SURFACE_CONTAINER_LOWEST")""",
      TextNode("VALUE_SURFACE_CONTAINER_LOWEST"),
    )
    assertSimplify("""dynamicColor("PRIMARY_FIXED")""", TextNode("VALUE_PRIMARY_FIXED"))
    assertSimplify("""dynamicColor("PRIMARY_FIXED_DIM")""", TextNode("VALUE_PRIMARY_FIXED_DIM"))
    assertSimplify("""dynamicColor("ON_PRIMARY_FIXED")""", TextNode("VALUE_ON_PRIMARY_FIXED"))
    assertSimplify(
      """dynamicColor("ON_PRIMARY_FIXED_VARIANT")""",
      TextNode("VALUE_ON_PRIMARY_FIXED_VARIANT"),
    )
    assertSimplify("""dynamicColor("SECONDARY_FIXED")""", TextNode("VALUE_SECONDARY_FIXED"))
    assertSimplify("""dynamicColor("SECONDARY_FIXED_DIM")""", TextNode("VALUE_SECONDARY_FIXED_DIM"))
    assertSimplify("""dynamicColor("ON_SECONDARY_FIXED")""", TextNode("VALUE_ON_SECONDARY_FIXED"))
    assertSimplify(
      """dynamicColor("ON_SECONDARY_FIXED_VARIANT")""",
      TextNode("VALUE_ON_SECONDARY_FIXED_VARIANT"),
    )
    assertSimplify("""dynamicColor("TERTIARY_FIXED")""", TextNode("VALUE_TERTIARY_FIXED"))
    assertSimplify("""dynamicColor("TERTIARY_FIXED_DIM")""", TextNode("VALUE_TERTIARY_FIXED_DIM"))
    assertSimplify("""dynamicColor("ON_TERTIARY_FIXED")""", TextNode("VALUE_ON_TERTIARY_FIXED"))
    assertSimplify(
      """dynamicColor("ON_TERTIARY_FIXED_VARIANT")""",
      TextNode("VALUE_ON_TERTIARY_FIXED_VARIANT"),
    )
  }

  @Test
  fun simplify_formatTimestamp() =
    assertSimplify("""formatTimestamp(123, "format")""", TextNode("format 123"))

  @Test
  fun simplify_colorScheme() =
    assertSimplify(
      """colorScheme("PRIMARY", "file://./commonTest/image.png")""",
      TextNode("PRIMARY_COLOR_FROM_file://./commonTest/image.png"),
    )

  private fun assertSimplify(input: String, expected: ASTNode?) {
    assertSimplifySingleRule(
      rule = SimplifyFunction,
      simplificationType = SimplificationType.EVAL_FUNCTION,
      input = input,
      expected = expected,
    )
  }
}

import kotlin.io.path.Path
import kotlin.io.path.copyTo
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.name
import kotlin.io.path.readText

plugins {
  id("sukko.multiplatform.library")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.serialization)
}

kotlin {
  android.namespace = "io.github.sadellie.sukko.core.iconfiles"
  sourceSets.commonMain.dependencies {
    implementation(libs.org.jetbrains.compose.components.components.resources)
    implementation(libs.org.jetbrains.compose.ui.ui.tooling.preview)
    implementation(libs.org.jetbrains.compose.foundation.foundation)
    implementation(libs.co.touchlab.kermit)
    implementation(libs.com.squareup.okio.okio)
    implementation(libs.io.coil.kt.coil3.coil.compose)
    implementation(libs.io.coil.kt.coil3.coil.svg)
    implementation(libs.org.jetbrains.compose.material3.material3)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))
    implementation(project(":material-symbols"))
  }
}

compose.resources.generateResClass = compose.resources.never

// TODO run/check before release builds
tasks.register("generateIconPackFiles") {
  group = "custom"
  description = "Generate icon pack"

  val directoryParam = project.properties["directory"] as? String
  val outputParam = project.properties["output"] as? String
  val styleParam = project.properties["style"] as? String
  val sizeParam = project.properties["size"] as? String

  doLast {
    val directoryPath = Path(directoryParam ?: error("missing directory")).toAbsolutePath()
    println("directoryPath: $directoryPath")
    val outputPath = Path(outputParam ?: error("missing output")).toAbsolutePath()
    println("outputPath: $outputPath")
    val style = styleParam ?: "rounded"
    println("style $style")
    val size = sizeParam ?: "24px"
    println("size $size")
    val defaultSvgPrefix = "_$size.svg"
    val filledSvgPrefix = "_fill1_$size.svg"

    val symbolsDirs =
      directoryPath.toFile().listFiles()?.filter { it.isDirectory } ?: error("Empty folder")
    symbolsDirs.forEach {
      val symbolName = it.name
      val styleDirectory = Path(it.absolutePath) / Path("materialsymbols$style")

      // "directory\1k\materialsymbolsrounded\1k_24px.svg"
      val defaultSvgPath = styleDirectory / "$symbolName$defaultSvgPrefix"
      // "directory\1k\materialsymbolsrounded\1k_fill1_24px.svg"
      val filledSvgPath = styleDirectory / Path("$symbolName$filledSvgPrefix")

      require(defaultSvgPath.exists()) { "$defaultSvgPath not found" }
      require(filledSvgPath.exists()) { "$filledSvgPath not found" }

      val defaultSvgTargetPath = outputPath / defaultSvgPath.name
      println("Copy $defaultSvgPath -> $defaultSvgTargetPath")
      defaultSvgPath.copyTo(defaultSvgTargetPath)

      val isSameInFilled = defaultSvgPath.readText() == filledSvgPath.readText()
      if (!isSameInFilled) {
        println("$symbolName is not same in filled")
        val filledSvgTargetPath = outputPath / filledSvgPath.name
        println("Copy $filledSvgPath -> $filledSvgTargetPath")
        filledSvgPath.copyTo(filledSvgTargetPath)
      }
    }
  }
}

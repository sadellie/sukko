plugins {
  id("sukko.multiplatform.library")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.serialization)
}

kotlin {
  android.namespace = "io.github.sadellie.sukko.core.script"
  sourceSets.commonMain.dependencies {
    implementation(libs.org.jetbrains.compose.components.components.resources)
    implementation(libs.org.jetbrains.compose.runtime.runtime)
    implementation(libs.com.squareup.okio.okio)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
  }
  sourceSets.commonTest.dependencies {
    implementation(libs.kotlin.test)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
  }
  sourceSets.androidHostTest.dependencies {
    implementation(libs.kotlin.test)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
  }
}

compose.resources {
  packageOfResClass = "io.github.sadellie.sukko.script.resources"
  publicResClass = false
  val scriptingFile = project.file("src/commonMain/composeResources/files/scripting.json")
  if (!scriptingFile.exists()) {
    logger.warn("w: Missing ${scriptingFile.absolutePath}")
  } else {
    val sourceFile = rootProject.file("docs/scripting.json")
    val isUpdated = scriptingFile.lastModified() == sourceFile.lastModified()
    if (!isUpdated) {
      logger.warn("w: Outdated ${scriptingFile.absolutePath}. Run :core:script:updateScriptingJson")
    }
  }
}

tasks.register("updateScriptingJson") {
  val sourceFile = rootProject.file("docs/scripting.json")
  val targetFile = project.file("src/commonMain/composeResources/files/scripting.json")
  doLast {
    if (!sourceFile.exists()) error("missing: ${sourceFile.absolutePath}")
    println("Copy ${sourceFile.absolutePath} -> ${targetFile.absolutePath}")
    sourceFile.copyTo(targetFile, overwrite = true)
    targetFile.setLastModified(sourceFile.lastModified())
  }
}

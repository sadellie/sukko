import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  // decouple from main app for multiplatform unitto
  alias(libs.plugins.android.library)
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.multiplatform)
}

kotlin {
  androidTarget {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
  }
  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    outputModuleName.set("composeApp")
    browser()
    binaries.executable()
  }

  sourceSets.commonMain.dependencies {
    implementation(compose.components.resources)
    implementation(compose.components.uiToolingPreview)
    implementation(compose.foundation)
    implementation(libs.com.materialkolor.material.color.utilities)
    implementation(libs.org.jetbrains.compose.material3.material3)
  }
  compilerOptions { optIn.addAll("androidx.compose.material3.ExperimentalMaterial3ExpressiveApi") }
}

compose.resources.generateResClass = compose.resources.never

android {
  namespace = "io.github.sadellie.themmo"
  defaultConfig.minSdk = 31
  defaultConfig.targetSdk = 36
  compileSdk = defaultConfig.targetSdk
}

plugins {
  id("sukko.multiplatform.library")
  alias(libs.plugins.serialization)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.compose)
}

kotlin {
  android.namespace = "io.github.sadellie.sukko.core.routes"
  sourceSets.commonMain.dependencies {
    implementation(libs.androidx.navigation3.navigation3.runtime)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
    implementation(libs.org.jetbrains.compose.runtime.runtime)
  }
}

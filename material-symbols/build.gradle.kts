plugins {
  id("sukko.multiplatform.library")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
}

kotlin {
  android.namespace = "google.material.design.symbols"
  sourceSets.commonMain.dependencies { implementation(libs.org.jetbrains.compose.ui.ui) }
}

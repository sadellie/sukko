plugins {
  id("sukko.multiplatform.library")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
}

kotlin { sourceSets.commonMain.dependencies { implementation(compose.ui) } }

android { namespace = "google.material.design.symbols" }

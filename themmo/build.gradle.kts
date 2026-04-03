plugins {
  id("sukko.multiplatform.library")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
}

kotlin {
  android.namespace = "io.github.sadellie.themmo"
  sourceSets.commonMain.dependencies {
    implementation(libs.org.jetbrains.compose.ui.ui.tooling.preview)
    implementation(libs.org.jetbrains.compose.foundation.foundation)
    implementation(libs.org.jetbrains.compose.material3.material3)
    implementation(libs.com.materialkolor.material.color.utilities)
  }
}

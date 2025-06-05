plugins {
  id("sukko.multiplatform.library")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
}

kotlin {
  sourceSets.commonMain.dependencies {
    implementation(compose.components.uiToolingPreview)
    implementation(compose.foundation)
    implementation(libs.com.squareup.okio.okio)
    implementation(libs.io.coil.kt.coil3.coil.compose)
    implementation(libs.io.coil.kt.coil3.coil.svg)
    implementation(libs.org.jetbrains.compose.material3.material3)
    implementation(libs.org.jetbrains.compose.material3.material3.window.size)
    implementation(project(":themmo"))
  }
}

android { namespace = "io.github.sadellie.sukko.core.designsystem" }

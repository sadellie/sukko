plugins {
  id("sukko.multiplatform.library")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
}

kotlin {
  sourceSets.commonMain.dependencies {
    implementation(compose.components.resources)
    implementation(compose.components.uiToolingPreview)
    implementation(compose.foundation)
    implementation(libs.com.composables.core)
    implementation(libs.io.coil.kt.coil3.coil.compose)
    implementation(libs.org.jetbrains.compose.material3.material3)
    implementation(libs.org.jetbrains.compose.material3.material3.window.size)
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":material-symbols"))
  }
  sourceSets.androidMain.dependencies {
    implementation(compose.uiTooling)
    implementation(libs.androidx.activity.compose)
  }
}

compose.resources.generateResClass = compose.resources.never

android { namespace = "io.github.sadellie.sukko.core.ui" }

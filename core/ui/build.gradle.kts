plugins {
  id("sukko.multiplatform.library")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
}

kotlin {
  android.namespace = "io.github.sadellie.sukko.core.ui"
  sourceSets.commonMain.dependencies {
    implementation(libs.org.jetbrains.compose.components.components.resources)
    implementation(libs.org.jetbrains.compose.ui.ui.tooling.preview)
    implementation(libs.org.jetbrains.compose.ui.ui)
    implementation(libs.org.jetbrains.compose.foundation.foundation)
    implementation(libs.com.composables.core)
    implementation(libs.io.coil.kt.coil3.coil.compose)
    implementation(libs.org.jetbrains.compose.material3.material3)
    implementation(libs.org.jetbrains.compose.material3.material3.window.size)
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":material-symbols"))
  }
  sourceSets.androidMain.dependencies {
    implementation(libs.org.jetbrains.compose.ui.ui.tooling)
    implementation(libs.androidx.activity.compose)
  }
}

compose.resources.generateResClass = compose.resources.never

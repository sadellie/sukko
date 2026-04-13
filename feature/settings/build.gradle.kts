plugins {
  id("sukko.multiplatform.library")
  id("sukko.metro")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.serialization)
}

kotlin {
  android.namespace = "io.github.sadellie.sukko.feature.settings"
  sourceSets.commonMain.dependencies {
    implementation(libs.androidx.navigation3.navigation3.runtime)
    implementation(libs.co.touchlab.kermit)
    implementation(libs.com.composables.core)
    implementation(libs.com.squareup.okio.okio)
    implementation(libs.io.coil.kt.coil3.coil.compose)
    implementation(libs.io.github.vinceglb.filekit.dialogs.compose)
    implementation(libs.org.jetbrains.compose.components.components.resources)
    implementation(libs.org.jetbrains.compose.material3.material3)
    implementation(libs.org.jetbrains.compose.ui.ui)
    implementation(libs.androidx.core.core.ktx)
    implementation(libs.org.jetbrains.compose.ui.ui.tooling.preview)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:iconfiles"))
    implementation(project(":core:routes"))
    implementation(project(":core:ui"))
    implementation(project(":material-symbols"))
  }
}

compose.resources.generateResClass = compose.resources.never

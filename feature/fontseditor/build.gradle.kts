plugins {
  id("sukko.android.library")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.serialization)
}

android { namespace = "io.github.sadellie.sukko.feature.fontseditor" }

dependencies {
  implementation(project.dependencies.platform(libs.io.insert.koin.koin.bom))
  implementation(compose.components.resources)
  implementation(compose.components.uiToolingPreview)
  implementation(compose.ui)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.navigation3.navigation3.runtime)
  implementation(libs.co.touchlab.kermit)
  implementation(libs.com.composables.core)
  implementation(libs.com.squareup.okio.okio)
  implementation(libs.io.github.vinceglb.filekit.dialogs.compose)
  implementation(libs.io.insert.koin.koin.compose.viewmodel)
  implementation(libs.org.jetbrains.compose.material3.material3)
  implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
  implementation(project(":core:common"))
  implementation(project(":core:data"))
  implementation(project(":core:designsystem"))
  implementation(project(":core:fontfiles"))
  implementation(project(":core:model"))
  implementation(project(":core:ui"))
  implementation(project(":material-symbols"))
}

compose.resources.generateResClass = compose.resources.never

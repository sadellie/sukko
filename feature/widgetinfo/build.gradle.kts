plugins {
  id("sukko.multiplatform.library")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.serialization)
}

kotlin {
  android.namespace = "io.github.sadellie.sukko.feature.widgetinfo"
  sourceSets.commonMain.dependencies {
    implementation(libs.androidx.navigation3.navigation3.runtime)
    implementation(libs.co.touchlab.kermit)
    implementation(libs.com.composables.core)
    implementation(libs.io.insert.koin.koin.compose)
    implementation(libs.io.insert.koin.koin.compose.navigation3)
    implementation(libs.io.insert.koin.koin.compose.viewmodel)
    implementation(libs.io.insert.koin.koin.core)
    implementation(libs.org.jetbrains.compose.components.components.resources)
    implementation(libs.org.jetbrains.compose.material3.material3)
    implementation(libs.org.jetbrains.compose.ui.ui)
    implementation(libs.org.jetbrains.compose.ui.ui.tooling.preview)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:routes"))
    implementation(project(":core:ui"))
    implementation(project(":material-symbols"))
    implementation(project.dependencies.platform(libs.io.insert.koin.koin.bom))
  }
  sourceSets.androidMain.dependencies {
    implementation(libs.org.jetbrains.compose.ui.ui.tooling)
    implementation(libs.io.insert.koin.koin.core.coroutines)
  }
}

compose.resources.generateResClass = compose.resources.never

plugins {
  id("sukko.multiplatform.library")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.serialization)
}

kotlin {
  android.namespace = "io.github.sadellie.sukko.core.filefiles"
  sourceSets.commonMain.dependencies {
    implementation(libs.org.jetbrains.compose.components.components.resources)
    implementation(libs.org.jetbrains.compose.ui.ui.tooling.preview)
    implementation(libs.org.jetbrains.compose.foundation.foundation)
    implementation(libs.co.touchlab.kermit)
    implementation(libs.com.squareup.okio.okio)
    implementation(libs.io.github.vinceglb.filekit.core)
    implementation(libs.org.jetbrains.compose.material3.material3)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
    implementation(project.dependencies.platform(libs.io.insert.koin.koin.bom))
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))
  }
  sourceSets.androidMain.dependencies {
    implementation(libs.io.insert.koin.koin.core.coroutines)
    implementation(libs.io.insert.koin.koin.android)
  }
}

compose.resources.generateResClass = compose.resources.never

plugins {
  id("sukko.android.library")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
}

android.namespace = "io.github.sadellie.sukko.core.unglance"

android.buildFeatures.compose = true

dependencies {
  implementation(project.dependencies.platform(libs.io.insert.koin.koin.bom))
  implementation(libs.org.jetbrains.compose.ui.ui)
  implementation(libs.androidx.concurrent.concurrent.futures.ktx)
  implementation(libs.androidx.lifecycle.lifecycle.process)
  implementation(libs.androidx.work.work.runtime.ktx)
  implementation(libs.co.touchlab.kermit)
  implementation(libs.com.squareup.okio.okio)
  implementation(libs.io.coil.kt.coil3.coil.compose)
  implementation(libs.io.insert.koin.koin.compose)
  implementation(libs.io.insert.koin.koin.core)
  implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
  implementation(project(":core:common"))
  implementation(project(":core:designsystem"))
  implementation(project(":core:data"))
}

plugins {
  id("sukko.android.library")
  alias(libs.plugins.serialization)
}

android {
  namespace = "io.github.sadellie.sukko.core.importexport"
  defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
}

dependencies {
  implementation(libs.co.touchlab.kermit)
  implementation(libs.com.squareup.okio.okio)
  implementation(libs.io.github.vinceglb.filekit.core)
  implementation(libs.io.insert.koin.koin.android)
  implementation(libs.io.insert.koin.koin.core.coroutines)
  implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
  implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
  implementation(project(":core:common"))
  implementation(project(":core:data"))
  implementation(project(":core:fontfiles"))
  implementation(project(":core:iconfiles"))
  implementation(project.dependencies.platform(libs.io.insert.koin.koin.bom))
  testImplementation(libs.kotlin.test)
  testImplementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
  androidTestImplementation(libs.androidx.room.testing)
  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(project(":core:database"))
}

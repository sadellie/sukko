plugins {
  id("sukko.multiplatform.library")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.serialization)
}

kotlin {
  sourceSets.commonMain.dependencies {
    implementation(compose.components.resources)
    implementation(compose.components.uiToolingPreview)
    implementation(compose.foundation)
    implementation(libs.co.touchlab.kermit)
    implementation(libs.com.squareup.okio.okio)
    implementation(libs.io.coil.kt.coil3.coil.compose)
    implementation(libs.org.jetbrains.compose.material3.material3)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.datetime)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:fontfiles"))
    implementation(project(":core:iconfiles"))
    implementation(project(":core:script"))
    implementation(project(":material-symbols"))
  }
  sourceSets.commonTest.dependencies {
    implementation(libs.kotlin.test)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
  }
}

compose.resources.generateResClass = compose.resources.never

android { namespace = "io.github.sadellie.sukko.core.model" }

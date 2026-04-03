plugins {
  id("sukko.multiplatform.library")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.serialization)
}

kotlin {
  android.namespace = "io.github.sadellie.sukko.core.common"
  sourceSets.commonMain.dependencies {
    implementation(libs.co.touchlab.kermit)
    implementation(libs.com.squareup.okio.okio)
    implementation(libs.io.github.vinceglb.filekit.dialogs)
    implementation(libs.org.jetbrains.compose.components.components.resources)
    implementation(libs.org.jetbrains.compose.foundation.foundation)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
  }
  sourceSets.commonTest.dependencies { implementation(libs.kotlin.test) }
}

compose.resources {
  packageOfResClass = "io.github.sadellie.sukko.resources"
  publicResClass = true
}

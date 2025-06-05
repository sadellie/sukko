plugins {
  id("sukko.multiplatform.library")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.serialization)
}

kotlin {
  sourceSets.commonMain.dependencies {
    implementation(compose.components.resources)
    implementation(compose.foundation)
    implementation(libs.co.touchlab.kermit)
    implementation(libs.com.squareup.okio.okio)
    implementation(libs.io.github.vinceglb.filekit.dialogs)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
  }
  sourceSets.androidMain.dependencies { implementation(libs.androidx.core.ktx) }
  sourceSets.commonTest.dependencies { implementation(libs.kotlin.test) }
}

compose.resources {
  packageOfResClass = "io.github.sadellie.sukko.resources"
  publicResClass = true
}

android { namespace = "io.github.sadellie.sukko.core.common" }

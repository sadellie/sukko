plugins {
  id("sukko.multiplatform.library")
  id("sukko.metro")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
}

kotlin {
  android.namespace = "io.github.sadellie.sukko.feature.widget"
  sourceSets.commonMain.dependencies {
    implementation(libs.org.jetbrains.compose.ui.ui)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
    implementation(libs.org.jetbrains.compose.material3.material3.window.size)
    implementation(libs.io.coil.kt.coil3.coil.compose)
    implementation(libs.androidx.navigation3.navigation3.runtime)
    implementation(project(":core:routes"))
    implementation(project(":core:fontfiles"))
    implementation(project(":core:routes-ui"))
  }
  sourceSets.androidMain.dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat.appcompat)
    implementation(libs.co.touchlab.kermit)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.datetime)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:unglance"))
  }
  sourceSets.commonTest.dependencies { implementation(libs.kotlin.test) }
}

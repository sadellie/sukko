plugins {
  id("sukko.multiplatform.library")
  id("sukko.metro")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.serialization)
}

kotlin {
  android.namespace = "io.github.sadellie.sukko.feature.editor"
  sourceSets.commonMain.dependencies {
    implementation(libs.androidx.navigation3.navigation3.runtime)
    implementation(libs.co.touchlab.kermit)
    implementation(libs.com.composables.core)
    implementation(libs.dev.zacsweers.metro.metrox.viewmodel.compose)
    implementation(libs.io.coil.kt.coil3.coil.compose)
    implementation(libs.io.coil.kt.coil3.coil.svg)
    implementation(libs.io.github.pingpongboss.compose.exploded.layers)
    implementation(libs.io.github.vinceglb.filekit.dialogs.compose)
    implementation(libs.org.jetbrains.compose.components.components.resources)
    implementation(libs.org.jetbrains.compose.material3.material3)
    implementation(libs.org.jetbrains.compose.material3.material3.window.size)
    implementation(libs.org.jetbrains.compose.ui.ui)
    implementation(libs.org.jetbrains.compose.ui.ui.tooling.preview)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
    implementation(libs.sh.calvin.reorderable.reorderable)
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:fontfiles"))
    implementation(project(":core:iconfiles"))
    implementation(project(":core:routes"))
    implementation(project(":core:ui"))
    implementation(project(":feature:widget"))
    implementation(project(":material-symbols"))
  }
  sourceSets.androidMain.dependencies { implementation(libs.org.jetbrains.compose.ui.ui.tooling) }
  sourceSets.commonTest.dependencies {
    implementation(libs.kotlin.test)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
  }
}

compose.resources.generateResClass = compose.resources.never

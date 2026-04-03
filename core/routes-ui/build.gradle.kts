plugins {
  id("sukko.multiplatform.library")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
}

kotlin {
  android.namespace = "io.github.sadellie.sukko.core.routes.ui"
  sourceSets.commonMain.dependencies {
    implementation(libs.androidx.navigation3.navigation3.runtime)
    implementation(libs.io.coil.kt.coil3.coil.core)
    implementation(libs.io.insert.koin.koin.compose.navigation3)
    implementation(libs.io.insert.koin.koin.core)
    implementation(libs.org.jetbrains.androidx.lifecycle.lifecycle.viewmodel.navigation3)
    implementation(libs.org.jetbrains.androidx.navigation3.navigation3.ui)
    implementation(libs.org.jetbrains.compose.material3.material3)
    implementation(libs.org.jetbrains.compose.material3.material3.window.size)
    implementation(project(":core:designsystem"))
    implementation(project(":core:routes"))
    implementation(project(":themmo"))
    implementation(project.dependencies.platform(libs.io.insert.koin.koin.bom))
  }
}

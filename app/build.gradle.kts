plugins {
  id("sukko.multiplatform.library")
  id("sukko.metro")
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.compose)
}

kotlin {
  android.namespace = "io.github.sadellie.sukko.shared"
  sourceSets {
    commonMain.dependencies {
      implementation(libs.androidx.navigation3.navigation3.runtime)
      implementation(libs.co.touchlab.kermit)
      implementation(libs.io.coil.kt.coil3.coil.compose)
      implementation(libs.io.coil.kt.coil3.coil.core)
      implementation(libs.org.jetbrains.compose.material3.material3)
      implementation(libs.org.jetbrains.compose.material3.material3.window.size)
      implementation(libs.dev.zacsweers.metro.metrox.viewmodel.compose)
      implementation(project(":core:common"))
      implementation(project(":core:data"))
      implementation(project(":core:database"))
      implementation(project(":core:designsystem"))
      implementation(project(":core:fontfiles"))
      implementation(project(":core:importexport"))
      implementation(project(":core:routes"))
      implementation(project(":core:routes-ui"))
      implementation(project(":core:ui"))
      implementation(project(":feature:home"))
    }
    androidMain.dependencies {
      implementation(project(":feature:editor"))
      implementation(project(":feature:fontseditor"))
      implementation(project(":feature:iconpackeditor"))
      implementation(project(":feature:importpreset"))
      implementation(project(":feature:presetselector"))
      implementation(project(":feature:saveaspreset"))
      implementation(project(":feature:settings"))
      implementation(project(":feature:widget"))
      implementation(project(":feature:widgetinfo"))
      implementation(libs.androidx.activity.compose)
      implementation(libs.androidx.appcompat.appcompat)
    }
  }
}

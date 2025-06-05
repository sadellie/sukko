plugins {
  id("sukko.multiplatform.library")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.serialization)
}

kotlin {
  sourceSets.commonMain.dependencies {
    implementation(project.dependencies.platform(libs.io.insert.koin.koin.bom))
    implementation(compose.components.resources)
    implementation(compose.components.uiToolingPreview)
    implementation(compose.ui)
    implementation(libs.androidx.navigation3.navigation3.runtime)
    implementation(libs.co.touchlab.kermit)
    implementation(libs.com.composables.core)
    implementation(libs.io.coil.kt.coil3.coil.compose)
    implementation(libs.io.coil.kt.coil3.coil.svg)
    implementation(libs.io.github.vinceglb.filekit.dialogs.compose)
    implementation(libs.io.insert.koin.koin.compose)
    implementation(libs.io.insert.koin.koin.compose.viewmodel)
    implementation(libs.io.insert.koin.koin.core)
    implementation(libs.org.jetbrains.compose.material3.material3)
    implementation(libs.org.jetbrains.compose.material3.material3.window.size)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
    implementation(libs.sh.calvin.reorderable.reorderable)
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:fontfiles"))
    implementation(project(":core:iconfiles"))
    implementation(project(":core:medialistener"))
    implementation(project(":core:model"))
    implementation(project(":core:script"))
    implementation(project(":core:ui"))
    implementation(project(":core:widget"))
    implementation(project(":material-symbols"))
  }
  sourceSets.androidMain.dependencies { implementation(compose.uiTooling) }
  sourceSets.commonTest.dependencies {
    implementation(libs.kotlin.test)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
  }
  sourceSets { all { languageSettings.enableLanguageFeature("WhenGuards") } }
}

compose.resources.generateResClass = compose.resources.never

android { namespace = "io.github.sadellie.sukko.feature.editor" }

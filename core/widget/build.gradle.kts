plugins {
  id("sukko.multiplatform.library")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
}

kotlin {
  sourceSets.commonMain.dependencies {
    implementation(compose.ui)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
  }
  sourceSets.androidMain.dependencies {
    implementation(project.dependencies.platform(libs.io.insert.koin.koin.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.co.touchlab.kermit)
    implementation(libs.io.insert.koin.koin.core)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.datetime)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:medialistener"))
    implementation(project(":core:model"))
    implementation(project(":core:unglance"))
  }
  sourceSets.commonTest.dependencies { implementation(libs.kotlin.test) }
}

android { namespace = "io.github.sadellie.sukko.core.widget" }

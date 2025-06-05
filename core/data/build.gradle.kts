plugins {
  id("sukko.multiplatform.library")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
}

kotlin {
  sourceSets.commonMain.dependencies {
    implementation(project.dependencies.platform(libs.io.insert.koin.koin.bom))
    implementation(compose.components.resources)
    implementation(compose.ui)
    implementation(libs.co.touchlab.kermit)
    implementation(libs.com.squareup.okio.okio)
    implementation(libs.io.github.vinceglb.filekit.core)
    implementation(libs.io.insert.koin.koin.core)
    implementation(libs.io.ktor.ktor.client.core)
    implementation(libs.org.jetbrains.compose.material3.material3)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.datetime)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
    implementation(project(":core:common"))
    implementation(project(":core:fontfiles"))
    implementation(project(":core:iconfiles"))
    implementation(project(":core:medialistener"))
    implementation(project(":core:model"))
    implementation(project(":core:remote"))
    implementation(project(":core:script"))
  }
  sourceSets.androidMain.dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.com.kmpalette.kmpalette.core)
    implementation(libs.com.materialkolor.material.color.utilities)
    implementation(project(":core:database"))
  }
  sourceSets.commonTest.dependencies {
    implementation(libs.kotlin.test)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
  }
  sourceSets.androidUnitTest.dependencies {
    implementation(libs.junit.junit)
    implementation(libs.androidx.test.ext.junit.ktx)
    implementation(libs.org.robolectric.robolectric)
  }
  sourceSets.androidInstrumentedTest.dependencies {
    implementation(libs.androidx.room.testing)
    implementation(libs.androidx.test.core)
    implementation(libs.androidx.test.runner)
  }
}

compose.resources.generateResClass = compose.resources.never

android { namespace = "io.github.sadellie.sukko.core.data" }

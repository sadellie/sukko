plugins {
  id("sukko.multiplatform.library")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.serialization)
}

kotlin {
  android.namespace = "io.github.sadellie.sukko.core.data"
  sourceSets.commonMain.dependencies {
    implementation(libs.co.touchlab.kermit)
    implementation(libs.com.squareup.okio.okio)
    implementation(libs.io.coil.kt.coil3.coil.compose)
    implementation(libs.io.coil.kt.coil3.coil.core)
    implementation(libs.io.coil.kt.coil3.coil.network.ktor3)
    implementation(libs.io.coil.kt.coil3.coil.network.cache.control)
    implementation(libs.io.github.vinceglb.filekit.core)
    implementation(libs.io.insert.koin.koin.core)
    implementation(libs.io.ktor.ktor.client.core)
    implementation(libs.org.jetbrains.compose.components.components.resources)
    implementation(libs.org.jetbrains.compose.foundation.foundation)
    implementation(libs.org.jetbrains.compose.material3.material3)
    implementation(libs.org.jetbrains.compose.ui.ui)
    implementation(libs.org.jetbrains.compose.ui.ui.tooling.preview)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.datetime)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:fontfiles"))
    implementation(project(":core:iconfiles"))
    implementation(project(":core:remote"))
    implementation(project(":core:script"))
    implementation(project(":material-symbols"))
    implementation(project.dependencies.platform(libs.io.insert.koin.koin.bom))
  }
  sourceSets.androidMain.dependencies {
    implementation(libs.androidx.media3.media3.datasource)
    implementation(libs.androidx.media3.media3.session)
    implementation(libs.com.kmpalette.kmpalette.core)
    implementation(libs.com.materialkolor.material.color.utilities)
    implementation(libs.io.insert.koin.koin.android)
    implementation(libs.io.insert.koin.koin.core.coroutines)
    implementation(project(":core:database"))
  }
  sourceSets.commonTest.dependencies {
    implementation(libs.kotlin.test)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
  }
  sourceSets.androidHostTest.dependencies {
    implementation(libs.androidx.test.ext.junit.ktx)
    implementation(libs.junit.junit)
    implementation(libs.org.robolectric.robolectric)
  }
  sourceSets.androidDeviceTest.dependencies {
    implementation(libs.androidx.room.testing)
    implementation(libs.androidx.test.core)
    implementation(libs.androidx.test.runner)
  }
}

compose.resources.generateResClass = compose.resources.never

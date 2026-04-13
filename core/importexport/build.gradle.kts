plugins {
  id("sukko.multiplatform.library")
  id("sukko.metro")
  alias(libs.plugins.serialization)
}

kotlin {
  android.namespace = "io.github.sadellie.sukko.core.importexport"
  sourceSets.androidMain.dependencies {
    implementation(libs.co.touchlab.kermit)
    implementation(libs.com.squareup.okio.okio)
    implementation(libs.io.github.vinceglb.filekit.core)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:fontfiles"))
    implementation(project(":core:iconfiles"))
    implementation(libs.androidx.core.core.ktx)
  }
  sourceSets.androidHostTest.dependencies {
    implementation(libs.kotlin.test)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
  }
  sourceSets.androidDeviceTest.dependencies {
    implementation(libs.androidx.room.testing)
    implementation(libs.androidx.test.core)
    implementation(libs.androidx.test.runner)
    implementation(project(":core:database"))
  }
}

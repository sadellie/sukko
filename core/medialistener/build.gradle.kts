plugins { id("sukko.multiplatform.library") }

kotlin {
  sourceSets.commonMain.dependencies {
    implementation(project.dependencies.platform(libs.io.insert.koin.koin.bom))
    implementation(libs.co.touchlab.kermit)
    implementation(libs.com.squareup.okio.okio)
    implementation(libs.io.insert.koin.koin.core)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
    implementation(project(":core:common"))
    implementation(project(":core:model"))
  }
  sourceSets.androidMain.dependencies {
    implementation(libs.androidx.core.ktx)
    implementation("androidx.media3:media3-session:1.8.0")
    implementation("androidx.media3:media3-datasource:1.8.0")
  }
}

android { namespace = "io.github.sadellie.sukko.core.medialistener" }

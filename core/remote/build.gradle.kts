plugins { id("sukko.multiplatform.library") }

kotlin {
  android.namespace = "io.github.sadellie.sukko.core.remote"
  sourceSets.commonMain.dependencies {
    implementation(libs.io.insert.koin.koin.core)
    implementation(libs.io.ktor.ktor.client.core)
    implementation(project.dependencies.platform(libs.io.insert.koin.koin.bom))
  }
  sourceSets.androidMain.dependencies {
    implementation(libs.io.insert.koin.koin.android)
    implementation(libs.io.insert.koin.koin.core.coroutines)
    implementation(libs.io.ktor.ktor.client.okhttp)
  }
}

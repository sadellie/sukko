plugins {
  id("sukko.multiplatform.library")
  id("sukko.metro")
}

kotlin {
  android.namespace = "io.github.sadellie.sukko.core.remote"
  sourceSets.commonMain.dependencies { implementation(libs.io.ktor.ktor.client.core) }
  sourceSets.androidMain.dependencies { implementation(libs.io.ktor.ktor.client.okhttp) }
}

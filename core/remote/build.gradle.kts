plugins { id("sukko.multiplatform.library") }

kotlin {
  sourceSets.commonMain.dependencies { implementation(libs.io.ktor.ktor.client.core) }
  sourceSets.androidMain.dependencies { implementation(libs.io.ktor.ktor.client.okhttp) }
}

android { namespace = "io.github.sadellie.sukko.core.remote" }

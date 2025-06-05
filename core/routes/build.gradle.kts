plugins { id("sukko.multiplatform.library") }

kotlin {
  sourceSets.commonMain.dependencies {
    implementation(libs.androidx.navigation3.navigation3.runtime)
  }
}

android { namespace = "io.github.sadellie.sukko.core.routes" }

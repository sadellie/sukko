plugins {
  id("sukko.multiplatform.library")
  id("sukko.metro")
  alias(libs.plugins.ksp)
  alias(libs.plugins.room)
}

kotlin {
  android.namespace = "io.github.sadellie.sukko.core.database"
  sourceSets.androidMain.dependencies {
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(project(":core:common"))
  }
  sourceSets.commonTest.dependencies { implementation(libs.kotlin.test) }
}

room {
  val schemaLocation = "$projectDir/schemas"
  schemaDirectory(schemaLocation)
  println("Exported Database schema to $schemaLocation")
}

dependencies { kspAndroid(libs.androidx.room.compiler) }

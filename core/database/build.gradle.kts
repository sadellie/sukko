plugins {
  id("sukko.multiplatform.library")
  alias(libs.plugins.ksp)
  alias(libs.plugins.room)
}

kotlin {
  android.namespace = "io.github.sadellie.sukko.core.database"
  sourceSets.androidMain.dependencies {
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(project.dependencies.platform(libs.io.insert.koin.koin.bom))
    implementation(libs.io.insert.koin.koin.core.coroutines)
    implementation(libs.io.insert.koin.koin.android)
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

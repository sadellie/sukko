plugins {
  id("sukko.android.library")
  alias(libs.plugins.ksp)
  alias(libs.plugins.room)
}

android { namespace = "io.github.sadellie.sukko.core.database" }

dependencies {
  ksp(libs.androidx.room.compiler)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(project(":core:common"))
  testImplementation(libs.kotlin.test)
  testImplementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
}

room {
  val schemaLocation = "$projectDir/schemas"
  schemaDirectory(schemaLocation)
  println("Exported Database schema to $schemaLocation")
}

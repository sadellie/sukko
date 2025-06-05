import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  `kotlin-dsl`
  alias(libs.plugins.serialization)
}

group = "io.github.sadellie.sukko.buildlogic"

java.sourceCompatibility = JavaVersion.VERSION_11

java.targetCompatibility = JavaVersion.VERSION_11

kotlin.compilerOptions.jvmTarget = JvmTarget.JVM_11

tasks {
  validatePlugins {
    enableStricterValidation = true
    failOnWarning = true
  }
}

dependencies {
  compileOnly(libs.android.gradlePlugin)
  compileOnly(libs.kotlin.gradlePlugin)
  compileOnly(libs.ksp.gradlePlugin)
  implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
}

gradlePlugin {
  plugins {
    register("SukkoMultiplatformLibraryPlugin") {
      id = "sukko.multiplatform.library"
      implementationClass = "SukkoMultiplatformLibraryPlugin"
    }
    register("SukkoAndroidLibraryPlugin") {
      id = "sukko.android.library"
      implementationClass = "SukkoAndroidLibraryPlugin"
    }
  }
}

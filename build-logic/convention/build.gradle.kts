import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins { `kotlin-dsl` }

group = "io.github.sadellie.sukko.buildlogic"

java.sourceCompatibility = JavaVersion.VERSION_21

java.targetCompatibility = JavaVersion.VERSION_21

kotlin.compilerOptions.jvmTarget = JvmTarget.JVM_21

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
  compileOnly(libs.metro.gradlePlugin)
}

gradlePlugin {
  plugins {
    register("SukkoMultiplatformLibraryPlugin") {
      id = "sukko.multiplatform.library"
      implementationClass = "SukkoMultiplatformLibraryPlugin"
    }
    register("SukkoMetroPlugin") {
      id = "sukko.metro"
      implementationClass = "SukkoMetroPlugin"
    }
  }
}

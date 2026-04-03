import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
}

kotlin {
  target { compilerOptions { jvmTarget.set(JvmTarget.JVM_21) } }
  dependencies {
    implementation(project(":app"))
    implementation(project(":feature:widget"))
  }
}

android {
  namespace = "io.github.sadellie.sukko"
  compileSdk = 36

  defaultConfig {
    applicationId = "io.github.sadellie.sukko"
    minSdk = 31
    targetSdk = 36
    versionCode = 2
    versionName = "experimental 2"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    debug {
      isDebuggable = true
      isMinifyEnabled = false
      isShrinkResources = false
      applicationIdSuffix = ".debug"
    }
    release {
      initWith(getByName("debug"))
      isDebuggable = false
      isMinifyEnabled = true
      isShrinkResources = true
      applicationIdSuffix = ""
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    // Explicit because I do not trust Android devs and so should you
    compose = true
    aidl = false
    shaders = false
    buildConfig = true
    resValues = false
  }
  packaging.resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
}

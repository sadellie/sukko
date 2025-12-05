import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.compose)
  alias(libs.plugins.multiplatform)
  alias(libs.plugins.android.application)
  alias(libs.plugins.room)
  alias(libs.plugins.serialization)
}

kotlin {
  androidTarget {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
  }
  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    outputModuleName.set("composeApp")
    browser {
      val rootDirPath = project.rootDir.path
      val projectDirPath = project.projectDir.path
      commonWebpackConfig {
        outputFileName = "composeApp.js"
        devServer =
          (devServer ?: KotlinWebpackConfig.DevServer()).apply {
            static =
              (static ?: mutableListOf()).apply {
                // Serve sources to debug inside browser
                add(rootDirPath)
                add(projectDirPath)
              }
          }
      }
      testTask { useKarma { useFirefoxHeadless() } }
    }
    binaries.executable()
  }
  sourceSets {
    commonMain.dependencies {
      implementation(project.dependencies.platform(libs.io.insert.koin.koin.bom))
      implementation(compose.components.resources)
      implementation(compose.components.uiToolingPreview)
      implementation(compose.ui)
      implementation(libs.co.touchlab.kermit)
      implementation(libs.com.squareup.okio.okio)
      implementation(libs.io.coil.kt.coil3.coil.core)
      implementation(libs.io.insert.koin.koin.compose)
      implementation(libs.io.insert.koin.koin.compose.viewmodel)
      implementation(libs.io.insert.koin.koin.core)
      implementation(libs.io.ktor.ktor.client.core)
      implementation(libs.org.jetbrains.compose.material3.material3)
      implementation(libs.org.jetbrains.compose.material3.material3.window.size)
      implementation(libs.org.jetbrains.kotlinx.kotlinx.datetime)
      implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
      implementation(project(":core:common"))
      implementation(project(":core:data"))
      implementation(project(":core:designsystem"))
      implementation(project(":core:fontfiles"))
      implementation(project(":core:iconfiles"))
      implementation(project(":core:medialistener"))
      implementation(project(":core:model"))
      implementation(project(":core:remote"))
      implementation(project(":core:routes"))
      implementation(project(":core:script"))
      implementation(project(":core:ui"))
      implementation(project(":core:widget"))
      implementation(project(":feature:editor"))
      implementation(project(":feature:presetselector"))
      implementation(project(":feature:saveaspreset"))
      implementation(project(":themmo"))
    }
    androidMain.dependencies {
      implementation(compose.uiTooling)
      implementation(libs.androidx.activity.compose)
      implementation(libs.androidx.appcompat.appcompat)
      implementation(libs.androidx.core.ktx)
      implementation(libs.androidx.lifecycle.lifecycle.viewmodel.navigation3)
      implementation(libs.androidx.navigation3.navigation3.ui)
      implementation(libs.androidx.room.ktx)
      implementation(libs.androidx.room.runtime)
      implementation(libs.io.insert.koin.koin.androidx.startup)
      implementation(libs.io.insert.koin.koin.core.coroutines)
      implementation(libs.io.ktor.ktor.client.okhttp)
      implementation(project(":core:database"))
      implementation(project(":core:importexport"))
      implementation(project(":feature:fontseditor"))
      implementation(project(":feature:home"))
      implementation(project(":feature:icopackeditor"))
      implementation(project(":feature:importpreset"))
      implementation(project(":feature:settings"))
    }
    wasmJsMain.dependencies {}
    commonTest.dependencies {
      implementation(libs.kotlin.test)
      implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
    }
    androidUnitTest.dependencies {}
    wasmJsTest.dependencies {}
    androidInstrumentedTest.dependencies {
      implementation(libs.androidx.room.testing)
      implementation(libs.androidx.test.core)
      implementation(libs.androidx.test.runner)
    }
  }

  compilerOptions {
    optIn.addAll(
      "kotlin.time.ExperimentalTime",
      "androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
    )
  }
  targets.configureEach {
    compilations.configureEach {
      compileTaskProvider.get().compilerOptions { freeCompilerArgs.add("-Xexpect-actual-classes") }
    }
  }
}

android {
  namespace = "io.github.sadellie.sukko"
  compileSdk = 36

  defaultConfig {
    applicationId = "io.github.sadellie.sukko"
    minSdk = 31
    targetSdk = 36
    versionCode = 1
    versionName = "experimental 1"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    debug {
      isDebuggable = true
      isMinifyEnabled = false
      isShrinkResources = false
      applicationIdSuffix = ""
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
    renderScript = false
    shaders = false
    buildConfig = true
    resValues = false
  }
  packaging.resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
}

room {
  val schemaLocation = "$projectDir/schemas"
  schemaDirectory(schemaLocation)
  println("Exported Database schema to $schemaLocation")
}

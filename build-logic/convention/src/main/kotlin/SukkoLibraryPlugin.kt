import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import kotlin.jvm.optionals.getOrNull

@Suppress("UNUSED")
class SukkoMultiplatformLibraryPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      apply(plugin = libs.getPlugin("multiplatform"))
      apply(plugin = libs.getPlugin("android.multiplatform.library"))
      extensions.configure<KotlinMultiplatformExtension> {
        configure<KotlinMultiplatformAndroidLibraryTarget> {
          compileSdk = 36
          minSdk = 31
          @OptIn(ExperimentalKotlinGradlePluginApi::class)
          compilerOptions { jvmTarget.set(JvmTarget.JVM_21) }
          androidResources { enable = true }
          withDeviceTest { this.instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
          withHostTest {}
        }
        @OptIn(ExperimentalWasmDsl::class)
        wasmJs {
          browser { testTask { useKarma().useFirefoxHeadless() } }
          binaries.executable()
        }
        compilerOptions.optInExperimental()
        targets.configureEach {
          compilations.configureEach {
            compileTaskProvider.get().compilerOptions {
              freeCompilerArgs.add("-Xexpect-actual-classes")
            }
          }
        }
      }
    }
  }
}

@Suppress("UNUSED")
class SukkoAndroidLibraryPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      apply(plugin = libs.getPlugin("android.library"))

      extensions.configure<LibraryExtension> {
        compileSdk = 36
        defaultConfig.minSdk = 31

        buildFeatures {
          compose = false
          aidl = false
          shaders = false
          buildConfig = false
          resValues = false
        }

        packaging.resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")

        compileOptions {
          sourceCompatibility = JavaVersion.VERSION_21
          targetCompatibility = JavaVersion.VERSION_21
        }

        configureKotlin<KotlinAndroidProjectExtension>()

        defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        defaultConfig.consumerProguardFiles("consumer-rules.pro")
      }

      dependencies {
        "testImplementation"(libs.findLibrary("junit.junit").get())

        "androidTestImplementation"(libs.findLibrary("androidx.test.core").get())
        "androidTestImplementation"(libs.findLibrary("androidx.test.ext.junit.ktx").get())
        "androidTestImplementation"(libs.findLibrary("androidx.test.runner").get())
      }
    }
  }
}

private val Project.libs: VersionCatalog
  get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

private fun VersionCatalog.getPlugin(name: String) =
  findPlugin(name).getOrNull()?.get()?.pluginId ?: error("$name not found")

/** Configure base Kotlin options */
private inline fun <reified T : KotlinBaseExtension> Project.configureKotlin() =
  configure<T> {
    // Treat all Kotlin warnings as errors (disabled by default)
    // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
    val warningsAsErrors: String? by project
    val compilerOptions =
      when (this) {
        is KotlinAndroidProjectExtension -> compilerOptions
        is KotlinJvmProjectExtension -> compilerOptions
        else -> error("Unsupported project extension $this ${T::class}")
      }

    compilerOptions.jvmTarget = JvmTarget.JVM_21
    compilerOptions.allWarningsAsErrors = warningsAsErrors.toBoolean()
    compilerOptions.optInExperimental()
  }

private fun KotlinCommonCompilerOptions.optInExperimental() {
  optIn.addAll(
    "androidx.compose.material3.ExperimentalMaterial3Api",
    "androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
    "androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi",
    "kotlinx.coroutines.ExperimentalCoroutinesApi",
  )
}

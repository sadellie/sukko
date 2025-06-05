import com.android.build.api.dsl.LibraryExtension
import kotlin.jvm.optionals.getOrNull
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("UNUSED")
class SukkoMultiplatformLibraryPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      apply(plugin = libs.getPlugin("multiplatform"))
      apply(plugin = libs.getPlugin("android.library"))

      extensions.configure<KotlinMultiplatformExtension> {
        androidTarget {
          @OptIn(ExperimentalKotlinGradlePluginApi::class)
          compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
        }
        @OptIn(ExperimentalWasmDsl::class)
        wasmJs {
          outputModuleName.set("composeApp")
          browser()
          binaries.executable()
        }
        compilerOptions {
          optIn.addAll(
            "kotlin.time.ExperimentalTime",
            "androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
          )
        }
        targets.configureEach {
          compilations.configureEach {
            compileTaskProvider.get().compilerOptions {
              freeCompilerArgs.add("-Xexpect-actual-classes")
            }
          }
        }
      }
      extensions.configure<LibraryExtension> {
        defaultConfig.minSdk = 31
        compileSdk = 36
      }
    }
  }
}

@Suppress("UNUSED")
class SukkoAndroidLibraryPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      apply(plugin = libs.getPlugin("kotlin.android"))
      apply(plugin = libs.getPlugin("android.library"))
      extensions.configure<LibraryExtension> {
        defaultConfig.minSdk = 31
        compileSdk = 36
        compileOptions.sourceCompatibility = JavaVersion.VERSION_21
        compileOptions.targetCompatibility = JavaVersion.VERSION_21
      }
      extensions.configure<KotlinAndroidProjectExtension> {
        compilerOptions.optIn.addAll(
          "androidx.compose.material3.ExperimentalMaterial3ExpressiveApi"
        )
      }
    }
  }
}

private val Project.libs: VersionCatalog
  get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

private fun VersionCatalog.getPlugin(name: String) =
  findPlugin(name).getOrNull()?.get()?.pluginId ?: error("$name not found")

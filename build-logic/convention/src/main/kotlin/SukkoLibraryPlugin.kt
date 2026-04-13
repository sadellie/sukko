import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
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
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
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
          compileSdk = 37
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
class SukkoMetroPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) { apply(plugin = libs.getPlugin("metro")) }
}

private val Project.libs: VersionCatalog
  get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

private fun VersionCatalog.getPlugin(name: String) =
  findPlugin(name).getOrNull()?.get()?.pluginId ?: error("$name not found")

private fun KotlinCommonCompilerOptions.optInExperimental() {
  optIn.addAll(
    "androidx.compose.material3.ExperimentalMaterial3Api",
    "androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
    "androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi",
    "kotlinx.coroutines.ExperimentalCoroutinesApi",
  )
}

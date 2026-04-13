import dev.iurysouza.modulegraph.Theme
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.compose) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.multiplatform) apply false
  alias(libs.plugins.room) apply false
  alias(libs.plugins.serialization) apply false
  alias(libs.plugins.android.multiplatform.library) apply false
  alias(libs.plugins.metro) apply false
  alias(libs.plugins.dev.iurysouza.modulegraph) apply true
  alias(libs.plugins.detekt) apply true
}

// Use createModuleGraph
moduleGraphConfig {
  readmePath.set("./ARCHITECTURE.md")
  heading = "## Module ~~spaghetti~~ Graph"
  theme.set(Theme.DARK)
}

tasks.register("detektProjectReport", Detekt::class) {
  description = "Generate detekt report"

  buildUponDefaultConfig = true
  ignoreFailures = false
  parallel = false
  config = rootProject.files("/detekt/config.yml")
  baseline = rootProject.file("/detekt/baseline.xml")

  configureDetektCommon()

  reports {
    xml.required = false
    txt.required = false
    md.required = false
    sarif.required = false
    html.required = true
    html.outputLocation = rootProject.file("/detekt/report.html")
  }
}

tasks.register("detektProjectBaseline", DetektCreateBaselineTask::class) {
  description = "Update detekt baseline"

  buildUponDefaultConfig = true
  ignoreFailures = true
  parallel = false
  config = rootProject.files("/detekt/config.yml")
  baseline = rootProject.file("/detekt/baseline.xml")

  configureDetektCommon()
}

fun SourceTask.configureDetektCommon() {
  setSource(files(rootDir))
  include("**/*.kt")
  exclude(
    "**/*.kts",
    "**/resources/**",
    "**/build/**",
    "**/build-logic/**",
    "**/themmo/**", // separate project
    "**/androidx/**", // not mine
    "**/wasmJsMain/**", // planned platform, not used
    "**/*Test/**", // tests are allowed to have shenanigans
    "**/data/script/**",
  )
}

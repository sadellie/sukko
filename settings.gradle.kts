pluginManagement {
  includeBuild("build-logic")
  repositories {
    // wasm
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositories {
    // wasm
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
    mavenCentral()
  }
}

rootProject.name = "sukko"

with(this) {
  // wrap for ktfmt
  include(":androidApp")
  include(":app")
  include(":core:common")
  include(":core:data")
  include(":core:database")
  include(":core:designsystem")
  include(":core:fontfiles")
  include(":core:iconfiles")
  include(":core:importexport")
  include(":core:remote")
  include(":core:routes")
  include(":core:routes-ui")
  include(":core:script")
  include(":core:ui")
  include(":core:unglance")
  include(":feature:editor")
  include(":feature:fontseditor")
  include(":feature:home")
  include(":feature:iconpackeditor")
  include(":feature:importpreset")
  include(":feature:presetselector")
  include(":feature:saveaspreset")
  include(":feature:settings")
  include(":feature:widget")
  include(":feature:widgetinfo")
  include(":material-symbols")
  include(":themmo")
}

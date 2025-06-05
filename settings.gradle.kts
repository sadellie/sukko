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
  include(":app")
  include(":core:common")
  include(":core:model")
  include(":core:script")
  include(":core:data")
  include(":core:unglance")
  include(":core:database")
  include(":core:medialistener")
  include(":core:remote")
  include(":core:widget")
  include(":core:importexport")
  include(":core:routes")
  include(":core:designsystem")
  include(":core:ui")
  include(":core:fontfiles")
  include(":core:iconfiles")
  include(":feature:editor")
  include(":feature:presetselector")
  include(":feature:saveaspreset")
  include(":feature:fontseditor")
  include(":feature:home")
  include(":feature:icopackeditor")
  include(":feature:importpreset")
  include(":feature:settings")
  include(":material-symbols")
  include(":themmo")
}

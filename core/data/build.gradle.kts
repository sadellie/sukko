import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

plugins {
  id("sukko.multiplatform.library")
  id("sukko.metro")
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.serialization)
}

kotlin {
  android.namespace = "io.github.sadellie.sukko.core.data"
  sourceSets.commonMain.dependencies {
    implementation(libs.androidx.datastore.datastore.preferences.core)
    implementation(libs.co.touchlab.kermit)
    implementation(libs.com.squareup.okio.okio)
    implementation(libs.io.coil.kt.coil3.coil.compose)
    implementation(libs.io.coil.kt.coil3.coil.core)
    implementation(libs.io.coil.kt.coil3.coil.network.cache.control)
    implementation(libs.io.coil.kt.coil3.coil.network.ktor3)
    implementation(libs.io.github.pingpongboss.compose.exploded.layers)
    implementation(libs.io.github.vinceglb.filekit.core)
    implementation(libs.io.ktor.ktor.client.core)
    implementation(libs.org.jetbrains.compose.components.components.resources)
    implementation(libs.org.jetbrains.compose.foundation.foundation)
    implementation(libs.org.jetbrains.compose.material3.material3)
    implementation(libs.org.jetbrains.compose.ui.ui)
    implementation(libs.org.jetbrains.compose.ui.ui.tooling.preview)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.datetime)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:fontfiles"))
    implementation(project(":core:iconfiles"))
    implementation(project(":core:remote"))
    implementation(project(":material-symbols"))
  }
  sourceSets.androidMain.dependencies {
    implementation(libs.androidx.datastore.datastore.preferences)
    implementation(libs.androidx.media3.media3.datasource)
    implementation(libs.androidx.media3.media3.session)
    implementation(libs.com.kmpalette.kmpalette.core)
    implementation(libs.com.materialkolor.material.color.utilities)
    implementation(libs.androidx.core.core.ktx)
    implementation(project(":core:database"))
  }
  sourceSets.commonTest.dependencies {
    implementation(libs.kotlin.test)
    implementation(libs.com.squareup.okio.okio.fakefilesystem)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
  }
  sourceSets.androidHostTest.dependencies {
    implementation(libs.androidx.test.ext.junit.ktx)
    implementation(libs.junit.junit)
  }
  sourceSets.androidDeviceTest.dependencies {
    implementation(libs.androidx.room.testing)
    implementation(libs.androidx.test.core)
    implementation(libs.androidx.test.runner)
  }
}

compose.resources {
  packageOfResClass = "io.github.sadellie.sukko.core.data.resources"
  publicResClass = false
}

tasks.register("updateScriptingMd") {
  val jsonFile = project.file("src/commonMain/composeResources/files/scripting.json")
  val mdFile = rootProject.file("docs/scripting.md")
  val bob = StringBuilder()

  fun extractTranslatableString(jsonObject: JsonObject, key: String): String {
    val field = jsonObject[key] ?: error("Field $key not found in $jsonObject")
    val translatedString = field.jsonObject["en"] ?: error("Translation $key not found in $field")
    return translatedString.jsonPrimitive.content
  }

  fun extractTypes(jsonObject: JsonObject, key: String): List<String> {
    val returnTypes = jsonObject[key] ?: error("types $key not found in $jsonObject")
    val returnTypesAsList = returnTypes.jsonArray.map { it.jsonPrimitive.content }
    return returnTypesAsList
  }

  fun parseConstants(jsonObject: JsonObject) {
    val name = extractTranslatableString(jsonObject, "name")
    val description = extractTranslatableString(jsonObject, "description")
    val api = jsonObject["api"]!!.jsonPrimitive.content
    val returnTypes = extractTypes(jsonObject, "returnTypes")

    bob.appendLine("#### $name $returnTypes")
    bob.appendLine()
    bob.appendLine(description)
    bob.appendLine()
    bob.appendLine("```kotlin")
    bob.appendLine(api)
    bob.appendLine("```")
    bob.appendLine()
  }

  fun parseMethods(jsonObject: JsonObject) {
    val name = extractTranslatableString(jsonObject, "name")
    val description = extractTranslatableString(jsonObject, "description")
    val api = jsonObject["api"]?.jsonPrimitive?.content ?: error("api not found in $jsonObject")
    val returnTypes = extractTypes(jsonObject, "returnTypes")
    val params = jsonObject["params"]?.jsonArray ?: error("params not found in $jsonObject")

    bob.appendLine("#### $name $returnTypes")
    bob.appendLine()
    bob.appendLine(description)
    bob.appendLine()
    bob.appendLine("```kotlin")
    bob.appendLine(api)
    bob.appendLine("```")
    bob.appendLine()

    params.forEach { element ->
      val obj = element.jsonObject
      val name = obj["name"]?.jsonPrimitive?.content ?: error("name not found $element")
      val description = extractTranslatableString(obj, "description")
      val types = extractTypes(obj, "types")
      bob.appendLine("- `$name` $types - $description")
    }
    bob.appendLine()
  }

  fun parseTemplates(jsonObject: JsonObject) {
    val name = extractTranslatableString(jsonObject, "name")
    val returnTypes = extractTypes(jsonObject, "returnTypes")
    val script = jsonObject["script"]!!.jsonPrimitive.content

    bob.appendLine("#### $name $returnTypes")
    bob.appendLine()
    bob.appendLine("```kotlin")
    bob.appendLine(script)
    bob.appendLine("```")
    bob.appendLine()
  }

  doLast {
    if (!jsonFile.exists()) error("Missing ${jsonFile.path}")
    if (!mdFile.exists()) error("Missing ${mdFile.path}")

    val jsonContent = Json.parseToJsonElement(jsonFile.readText()).jsonObject
    bob.appendLine()
    bob.appendLine()
    // constants
    val constants = jsonContent["constants"]?.jsonArray ?: error("constants not found")
    bob.appendLine("### Constants")
    bob.appendLine()
    constants.forEach { parseConstants(it.jsonObject) }

    // methods
    val methods = jsonContent["methods"]?.jsonArray ?: error("methods not found")
    bob.appendLine("### Methods")
    bob.appendLine()
    methods.forEach { parseMethods(it.jsonObject) }

    // templates
    val templates = jsonContent["templates"]?.jsonArray ?: error("templates not found")
    bob.appendLine("### Templates")
    bob.appendLine()
    templates.forEach { parseTemplates(it.jsonObject) }

    val apiDoc = bob.toString()
    val mdContent = mdFile.readText()
    val anchor = "## API"
    if (anchor !in mdContent) error("Missing anchor $anchor")
    val updatedMdContent = mdContent.replaceAfter(anchor, apiDoc)
    mdFile.writeText(updatedMdContent)
  }
}

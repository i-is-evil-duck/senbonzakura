import gg.meza.stonecraft.mod
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("gg.meza.stonecraft")
}

// This is the "central" build script: it runs once per build target,
// with `project.name` set to "<mc-version>-<loader>" (e.g. "1.21.1-neoforge").
val projectParts = project.name.split("-")
val mcVer = projectParts.getOrNull(0) ?: ""
val loader = projectParts.getOrNull(1) ?: ""

// ------------------------------------------------------------------
// Resource token replacement for Forge/NeoForge metadata.
// @MOD_MC_RANGE@ in META-INF/mods.toml / neoforge.mods.toml is filled
// with the mod_minecraft_range value from versions/dependencies/.
// (${id}, ${version}, ${name}, ... are expanded by Stonecraft itself.)
// ------------------------------------------------------------------
tasks.processResources {
    val tokens = mutableMapOf<String, String>()
    if (project.hasProperty("mod_minecraft_range")) {
        tokens["MOD_MC_RANGE"] = project.property("mod_minecraft_range").toString()
    }
    filesMatching("META-INF/mods.toml") {
        filter<ReplaceTokens>(mapOf("tokens" to tokens))
    }
    filesMatching("META-INF/neoforge.mods.toml") {
        filter<ReplaceTokens>(mapOf("tokens" to tokens))
    }
}

// ------------------------------------------------------------------
// Keep the release jar name stable across the build-system migration:
//   senbonzakura-<version>.jar  (same name ModDevGradle produced)
// ------------------------------------------------------------------
val releaseJarName = "${project.property("mod.id")}-${project.property("mod.version")}.jar"
tasks.named<org.gradle.api.tasks.bundling.Jar>("jar") {
    archiveFileName.set(releaseJarName)
}
tasks.matching { it.name == "remapJar" }.configureEach {
    val method = this.javaClass.getMethod("getArchiveFileName")
    @Suppress("UNCHECKED_CAST")
    val property = method.invoke(this) as org.gradle.api.provider.Property<String>
    property.set(releaseJarName)
}

// ------------------------------------------------------------------
// Stonecraft skips pack.mcmeta for NeoForge targets. Add it back so the
// jar keeps the same resource-pack metadata as the previous releases.
// ------------------------------------------------------------------
tasks.named("processResources") {
    doLast {
        val buildResources = project.layout.buildDirectory.dir("resources/main").get().asFile
        File(buildResources, "pack.mcmeta").writeText(buildString {
            appendLine("{")
            appendLine("  \"pack\": {")
            appendLine("    \"description\": \"${project.property("mod.name")} resources\",")
            appendLine("    \"pack_format\": 48")
            appendLine("  }")
            appendLine("}")
        })
    }
}

// ------------------------------------------------------------------
// NeoForge metadata fixup.
// NeoForge changed its metadata file name across versions:
//   - 1.20.x uses META-INF/mods.toml (with a "neoforge" dependency)
//   - 1.21+  uses META-INF/neoforge.mods.toml
// Stonecraft copies both files from sources verbatim, so this block
// rewrites them into the correct shape for the target version.
// ------------------------------------------------------------------
if (mod.isNeoforge) {
    tasks.named("processResources") {
        doLast {
            val buildResources = project.layout.buildDirectory.dir("resources/main").get().asFile
            val metaInf = File(buildResources, "META-INF")

            val isModernNeoForge = mcVer >= "1.21"

            val modId = project.property("mod.id")
            val modVersion = project.property("mod.version")
            val modName = project.property("mod.name")
            val modAuthors = project.property("mod.authors")
            val modDesc = project.property("mod.description")

            fun writeModToml(target: File, dependencyModId: String) {
                metaInf.mkdirs()
                target.writeText(buildString {
                    appendLine("modLoader = \"javafml\"")
                    appendLine("loaderVersion = \"*\"")
                    appendLine("license = \"${project.property("mod.license")}\"")
                    appendLine()
                    appendLine("[[mods]]")
                    appendLine("modId = \"$modId\"")
                    appendLine("version = \"$modVersion\"")
                    appendLine("displayName = \"$modName\"")
                    appendLine("authors = \"$modAuthors\"")
                    appendLine("description = '''")
                    appendLine(modDesc)
                    appendLine("'''")
                    appendLine()
                    appendLine("[[dependencies.$modId]]")
                    appendLine("modId = \"minecraft\"")
                    appendLine("mandatory = true")
                    appendLine("versionRange = \"${project.property("mod_minecraft_range")}\"")
                    appendLine("ordering = \"NONE\"")
                    appendLine("side = \"BOTH\"")
                    appendLine()
                    appendLine("[[dependencies.$modId]]")
                    appendLine("modId = \"$dependencyModId\"")
                    appendLine("mandatory = true")
                    appendLine("versionRange = \"*\"")
                    appendLine("ordering = \"NONE\"")
                    appendLine("side = \"BOTH\"")
                    appendLine()
                    appendLine("[[mixins]]")
                    appendLine("    config = \"senbonzakura.mixins.json\"")
                })
            }

            if (isModernNeoForge) {
                // NeoForge 1.21+ uses neoforge.mods.toml
                val targetFile = File(metaInf, "neoforge.mods.toml")
                if (!targetFile.exists()) {
                    writeModToml(targetFile, "neoforge")
                }
            } else {
                // NeoForge 1.20.x uses mods.toml with neoforge dependency
                val targetFile = File(metaInf, "mods.toml")
                if (!targetFile.exists()) {
                    writeModToml(targetFile, "neoforge")
                }
                // Remove neoforge.mods.toml that Stonecraft copies from source
                val wrongFile = File(metaInf, "neoforge.mods.toml")
                if (wrongFile.exists()) wrongFile.delete()
            }
        }
    }
}

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases/")
    }
}

plugins {
    id("gg.meza.stonecraft") version "1.10.+"
    id("dev.kikugie.stonecutter") version "0.9.+"
}

stonecutter {
    centralScript = "build.gradle.kts"
    kotlinController = true
    shared {
        fun mc(version: String, vararg loaders: String) {
            for (it in loaders) version("$version-$it", version)
        }

        // ============================================================
        // BUILD MATRIX
        //
        // Only the verified target (1.21.1-neoforge) is registered by
        // default. The rest of the matrix is kept here so adding a loader
        // or game version later is a one-line change: port that target's
        // platform-specific code, then build with
        //   ./gradlew build -PstonecutterAllTargets=true
        //
        // NOTE: the older Fabric API pins below predate Stonecraft's
        // gametest dependency and will fail dependency resolution until
        // they are updated as part of the Fabric port.
        // ============================================================
        val allTargets = providers.gradleProperty("stonecutterAllTargets")
            .map { it.toBoolean() }.getOrElse(false)

        if (allTargets) {
            mc("1.17.1", "fabric")
            mc("1.18.2", "forge", "fabric")
            mc("1.19.2", "forge", "fabric")
            mc("1.19.4", "fabric")
            mc("1.20.1", "forge", "fabric")
            mc("1.20.4", "fabric", "neoforge")
            mc("1.20.6", "fabric", "neoforge")
            mc("1.21.1", "forge", "fabric", "neoforge")
            mc("1.21.4", "fabric", "neoforge")
        } else {
            mc("1.21.1", "neoforge")
        }

        vcsVersion = "1.21.1-neoforge"
    }
    create(rootProject)
}

rootProject.name = "senbonzakura"

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar

plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("com.gradleup.shadow")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}


java {
    toolchain.languageVersion =
        JavaLanguageVersion.of(
            21
        )
}

val commonsLibDir = rootProject.extra["commonsLibDir"] as File
dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly(project(":KsiuCore"))
    compileOnly(project(":KsiuGUI"))
    implementation(fileTree(commonsLibDir) {
        include("StreamConnector-*.jar")
    })
}
val mainPackage = "com.ksiu.streambridge"
tasks {
    shadowJar {
        relocate("com.ksiu.commons", "$mainPackage.shadow.commons")
    }

    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion(
            "1.21.11"
        )
        jvmArgs(
            "-Xms2G",
            "-Xmx2G"
        )
    }

    processResources {
        val props =
            mapOf(
                "version" to version
            )
        filesMatching(
            "plugin.yml"
        ) {
            expand(
                props
            )
        }
    }

}

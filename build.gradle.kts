plugins{
    id("com.gradleup.shadow") version "9.4.1" apply false
}

subprojects {
    apply(plugin = "java")
    tasks.withType<Jar> {
        destinationDirectory.set(file("${rootProject.projectDir}/lib"))
    }
}

val commonsLibDir = file("${rootProject.projectDir}/../../Commons/lib")
extra["commonsLibDir"] = commonsLibDir
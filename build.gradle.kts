plugins {
    idea
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.9.23"
}

version = "1.0"

repositories {
    mavenCentral()
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
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
    // https://central.sonatype.com/artifact/io.qtjambi/qtjambi-native-windows-x64
    runtimeOnly("io.qtjambi:qtjambi-native-windows-x64:6.7.2")
    // https://central.sonatype.com/artifact/io.qtjambi/qtjambi-uic
    implementation("io.qtjambi:qtjambi-uic:6.7.2")
    // https://central.sonatype.com/artifact/io.qtjambi/qtjambi-uic-native-windows-x64
    runtimeOnly("io.qtjambi:qtjambi-uic-native-windows-x64:6.7.2")
    // https://mvnrepository.com/artifact/io.qtjambi/qtjambi-uitools
    implementation("io.qtjambi:qtjambi-uitools:6.7.2")
}

tasks.test {
    useJUnitPlatform()
}
tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
    archiveFileName.set("Qt-Jambi-Uic-Tool-${version}.jar")
}

kotlin {
    jvmToolchain(17)
}
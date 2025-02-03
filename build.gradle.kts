plugins {
    kotlin("jvm") version "2.0.20"
    id("edu.sc.seis.launch4j") version "3.0.6"
    java
    application
}

group = "io.github.fantazzj"
description = rootProject.name
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(files("lib/plantuml-1.2025.0.jar"))
    implementation("com.github.ajalt.clikt:clikt:5.0.2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "io.github.fantazzj.MainKt"
        attributes["Class-Path"] = configurations.runtimeClasspath.get().joinToString(separator = " ") { f -> f.name }
        println(attributes["Class-Path"])
    }
}

launch4j {
    dontWrapJar = true
    headerType = "console"
    outfile = "plantuml-conv.exe"
    priority = "normal"
    stayAlive = false
    restartOnCrash = false
    icon = "${projectDir}/res/main.ico"
    requires64Bit = false
    requiresJdk = false
    bundledJrePath = "jre"
    downloadUrl = ""
}

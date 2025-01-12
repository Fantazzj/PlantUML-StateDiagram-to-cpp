plugins {
    kotlin("jvm") version "2.0.20"
    application
}

group = "io.github.fantazzj"
description = "PlantUML-StateDiagram-to-cpp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(files("lib/plantuml-1.2024.8.jar"))
    implementation("com.github.ajalt.clikt:clikt:5.0.2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "io.github.fantazzj.MainKt"
}

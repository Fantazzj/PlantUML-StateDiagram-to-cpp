import org.jetbrains.kotlin.org.apache.commons.lang3.SystemUtils.IS_OS_LINUX
import org.jetbrains.kotlin.org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS

plugins {
    java
    application
    kotlin("jvm") version "2.0.20"
    id("edu.sc.seis.launch4j") version "3.0.6"
    id("org.beryx.runtime") version "1.13.1"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("net.sourceforge.plantuml:plantuml:1.2025.2")
    implementation("com.github.ajalt.clikt:clikt:5.0.2")
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass = "io.github.fantazzj.MainKt"
}

runtime {
    options.addAll("--strip-debug", "--no-header-files", "--no-man-pages")
    modules.addAll(
        "java.base",
        "java.compiler",
        "java.datatransfer",
        "java.desktop",
        "java.instrument",
        "java.logging",
        "java.management",
        "java.management.rmi",
        "java.naming",
        "java.net.http",
        "java.prefs",
        "java.rmi",
        "java.scripting",
        "java.se",
        "java.security.jgss",
        "java.security.sasl",
        "java.smartcardio",
        "java.sql",
        "java.sql.rowset",
        "java.transaction.xa",
        "java.xml",
        "java.xml.crypto"
    )
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

group = "io.github.fantazzj"
description = rootProject.name
version = "0.1"

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClass
        attributes["Class-Path"] = configurations.runtimeClasspath.get().joinToString(separator = " ") { it.name }
    }
}

if (IS_OS_WINDOWS)
    tasks.register<Zip>("createReleaseWindows") {
        description = "Create the release file for windows platform, includes jre and launch4j executable"
        group = "releases"
        dependsOn("createExe", "jre")
        from("LICENSE", "README.md")
        from("build/launch4j/")
        include("**")
        into("jre") {
            from("build/jre")
            include("**")
        }
        archiveFileName = "PlantUML-StateDiagram-to-cpp-windows.zip"
        destinationDirectory = file("build/releases")
    }

if (IS_OS_LINUX)
    tasks.register<Tar>("createReleaseLinux") {
        description = "Create the release file for linux platform, includes jre"
        group = "releases"
        dependsOn("installDist", "jre")
        from("LICENSE", "README.md")
        from("build/install/plantuml-statediagram-to-cpp/lib")
        include("**")
        into("jre") {
            from("build/jre")
            include("**")
        }
        compression = Compression.GZIP
        archiveFileName = "PlantUML-StateDiagram-to-cpp-linux.tar.gz"
        destinationDirectory = file("build/releases")
    }

val distZip by tasks
distZip.enabled = false
val distTar by tasks
distTar.enabled = false

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
    implementation(files("lib/plantuml-1.2025.0.jar"))
    implementation("com.github.ajalt.clikt:clikt:5.0.2")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass = "io.github.fantazzj.MainKt"
}

runtime {
    options.addAll("--strip-debug","--no-header-files", "--no-man-pages")
    modules.addAll("java.base","java.compiler","java.datatransfer","java.desktop","java.instrument","java.logging","java.management","java.management.rmi","java.naming","java.net.http","java.prefs","java.rmi","java.scripting","java.se","java.security.jgss","java.security.sasl","java.smartcardio","java.sql","java.sql.rowset","java.transaction.xa","java.xml","java.xml.crypto","jdk.accessibility","jdk.charsets","jdk.crypto.cryptoki","jdk.crypto.ec","jdk.crypto.mscapi","jdk.dynalink","jdk.httpserver","jdk.incubator.vector","jdk.internal.vm.ci","jdk.internal.vm.compiler","jdk.internal.vm.compiler.management","jdk.jdwp.agent","jdk.jfr","jdk.jsobject","jdk.localedata","jdk.management","jdk.management.agent","jdk.management.jfr","jdk.naming.dns","jdk.naming.rmi","jdk.net","jdk.nio.mapmode","jdk.sctp","jdk.security.auth","jdk.security.jgss","jdk.unsupported","jdk.xml.dom","jdk.zipfs")
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
        attributes["Class-Path"] = configurations.runtimeClasspath.get().joinToString(separator = " ") { f -> f.name }
    }
}

tasks.register<Zip>("createReleaseGithub") {
    group = "releases"
    dependsOn("createExe", "jre")
    from("build/launch4j/")
    include("**")
    into("jre") {
        from("build/jre")
        include("**")
    }
    archiveFileName.set("PlantUML-StateDiagram-to-cpp.zip")
    destinationDirectory.set(file("build/releases"))
}

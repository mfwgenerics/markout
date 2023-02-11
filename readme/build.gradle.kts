repositories {
    mavenCentral()
}

plugins {
    id("conventions")

    id("io.koalaql.markout")

    id("io.koalaql.kapshot-plugin") version "0.1.1"

    id("com.github.node-gradle.node") version "3.5.1"
}

markout {
    mainClass = "MainKt"
}

dependencies {
    implementation(project(":markout"))
    implementation(project(":markout-markdown"))
    implementation(project(":markout-docusaurus"))
    implementation(project(":markout-github-workflows-kt"))

    implementation(kotlin("reflect"))
}

node {
    nodeProjectDir.set(File("$rootDir/docusaurus"))
}

tasks.getByName("yarn_start") {
    dependsOn("yarn_install")
    dependsOn("markout")
}

tasks.register<DefaultTask>("installDocusaurus") {
    dependsOn("markout")
    dependsOn("yarn_start")
}
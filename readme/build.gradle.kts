import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    mavenCentral()
}

plugins {
    id("conventions")

    id("io.koalaql.markout")
    id("io.koalaql.kapshot-plugin") version "0.1.1"
}

val compileKotlin: KotlinCompile by tasks

compileKotlin.kotlinOptions.freeCompilerArgs += "-Xcontext-receivers"

markout {
    mainClass = "MainKt"
}

dependencies {
    implementation(project(":markout"))

    implementation(kotlin("reflect"))
}
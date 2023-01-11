repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") version "1.8.0"

    id("io.koalaql.kapshot-plugin") version "0.1.0"

    application
}

application {
    mainClass.set("MainKt")
}

dependencies {
    implementation(project(":markout"))

    implementation(kotlin("reflect"))
}
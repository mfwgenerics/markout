repositories {
    mavenCentral()
}

plugins {
    id("conventions")

    id("io.koalaql.kapshot-plugin") version "0.1.1"

    application
}

application {
    mainClass.set("MainKt")
}

dependencies {
    implementation(project(":markout"))

    implementation(kotlin("reflect"))
}
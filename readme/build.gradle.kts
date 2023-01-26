repositories {
    mavenCentral()
}

plugins {
    id("conventions")

    id("io.koalaql.markout-plugin")
    id("io.koalaql.kapshot-plugin") version "0.1.1"
}

dependencies {
    implementation(project(":markout"))

    implementation(kotlin("reflect"))
}
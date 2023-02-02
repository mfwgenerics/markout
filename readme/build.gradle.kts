repositories {
    mavenCentral()
}

plugins {
    id("conventions")

    id("io.koalaql.markout")

    id("io.koalaql.kapshot-plugin") version "0.1.1"
}

markout {
    mainClass = "MainKt"
}

dependencies {
    implementation(project(":markout"))
    implementation(project(":markout-markdown"))

    implementation(kotlin("reflect"))
}
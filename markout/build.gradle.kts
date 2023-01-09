repositories {
    mavenCentral()
}

plugins {
    id("publish")

    kotlin("jvm") version "1.8.0"
}

dependencies {
    /* deliberately include runtime without plugin */
    implementation("io.koalaql:kapshot-runtime:0.1.0")

    api(kotlin("reflect"))

    testImplementation(kotlin("test"))
}
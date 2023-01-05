repositories {
    mavenCentral()
}

plugins {
    id("publish")

    kotlin("jvm") version "1.8.0"

    application
}

application {
    mainClass.set("io.koalaql.markout.MainKt")
}

dependencies {
    api(kotlin("reflect"))

    testImplementation(kotlin("test"))
}
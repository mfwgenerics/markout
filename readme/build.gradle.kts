repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") version "1.8.0"

    id("io.koalaql.kapshot-plugin") version "0.0.2"

    application
}

application {
    mainClass.set("MainKt")
}

dependencies {
    implementation("io.koalaql:markout")

    implementation(kotlin("reflect"))
}
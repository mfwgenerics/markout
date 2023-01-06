repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") version "1.8.0"

    application
}

application {
    mainClass.set("MainKt")
}

dependencies {
    implementation("io.koalaql:markout")
    implementation(kotlin("reflect"))
}
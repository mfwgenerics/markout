repositories {
    mavenCentral()
}

plugins {
    java
    kotlin("jvm") version "1.8.10"

    id("io.koalaql.markout-markdown")
}

markout {
    mainClass = "MainKt"
}

dependencies {
    testImplementation(kotlin("test"))
}
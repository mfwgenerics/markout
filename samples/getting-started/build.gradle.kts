repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") version "1.8.10"

    id("io.koalaql.markout-markdown") version "0.0.9"
}

markout {
    mainClass = "myproject.MainKt"
}

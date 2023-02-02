repositories {
    mavenCentral()
}

plugins {
    id("publish")
}

dependencies {
    api(kotlin("reflect"))

    api(project(":markout"))

    testImplementation(kotlin("test"))
}
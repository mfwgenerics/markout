repositories {
    mavenCentral()
}

plugins {
    id("publish")
}

dependencies {
    api(kotlin("reflect"))

    testImplementation(kotlin("test"))
}
repositories {
    mavenCentral()
}

plugins {
    id("publish-1.8")
}

dependencies {
    api(kotlin("reflect"))

    api(project(":markout-markdown"))

    testImplementation(kotlin("test"))
}
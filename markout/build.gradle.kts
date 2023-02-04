repositories {
    mavenCentral()
}

plugins {
    id("publish-1.8")
}

dependencies {
    api(kotlin("reflect"))

    testImplementation(kotlin("test"))
}
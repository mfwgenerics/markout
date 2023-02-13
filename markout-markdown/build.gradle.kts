repositories {
    mavenCentral()
}

plugins {
    id("publish-1.8")
}

dependencies {
    api(kotlin("reflect"))

    api("io.koalaql:markout")

    testImplementation(kotlin("test"))
}
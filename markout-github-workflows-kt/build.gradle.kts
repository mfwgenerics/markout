repositories {
    mavenCentral()
}

plugins {
    id("publish-17")
}

dependencies {
    api(kotlin("reflect"))

    api(project(":markout"))

    api("it.krzeminski:github-actions-kotlin-dsl:0.35.0")

    testImplementation(kotlin("test"))
}
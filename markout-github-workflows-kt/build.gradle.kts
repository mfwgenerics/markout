repositories {
    mavenCentral()
}

plugins {
    id("publish-17")
}

dependencies {
    api(kotlin("reflect"))

    api("io.koalaql:markout")

    api("it.krzeminski:github-actions-kotlin-dsl:0.36.0")

    testImplementation(kotlin("test"))
}
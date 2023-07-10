repositories {
    mavenCentral()
}

plugins {
    id("publish-17")
}

dependencies {
    api(kotlin("reflect"))

    api("io.koalaql:markout")

    api("io.github.typesafegithub:github-workflows-kt:0.47.0")

    testImplementation(kotlin("test"))
}
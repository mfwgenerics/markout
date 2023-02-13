repositories {
    mavenCentral()
}

plugins {
    id("conventions")

    id("io.koalaql.markout")
    id("io.koalaql.markout-docusaurus")

    id("io.koalaql.kapshot-plugin") version "0.1.1"
}

markout {
    mainClass = "MainKt"
}

dependencies {
    implementation("io.koalaql:markout")
    implementation("io.koalaql:markout-markdown")
    implementation("io.koalaql:markout-docusaurus")
    implementation("io.koalaql:markout-github-workflows-kt")

    implementation(kotlin("reflect"))
}
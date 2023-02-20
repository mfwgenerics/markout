repositories {
    mavenCentral()
}

plugins {
    id("build-config")

    id("io.koalaql.markout-docusaurus")
}

markout {
    mainClass = "MainKt"
}

dependencies {
    implementation("io.koalaql:markout-github-workflows-kt")

    implementation(kotlin("reflect"))
}

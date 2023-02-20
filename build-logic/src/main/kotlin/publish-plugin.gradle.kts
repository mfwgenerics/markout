import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("build-config")

    id("java-gradle-plugin")
    id("com.gradle.plugin-publish")
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.getByName("publishPlugins") {
    doFirst {
        val version = "${project.version}"

        val isValid = !version.endsWith(".dirty")
            && version.count { it == '-' } < 2

        check(isValid) {
            "project.version ${project.version} does not appear to be a valid release version"
        }
    }
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin-api"))
}
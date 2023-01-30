import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("publish")
}

java {
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}
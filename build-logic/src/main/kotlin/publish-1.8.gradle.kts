import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("publish")
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
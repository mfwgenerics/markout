plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0")
    implementation("com.palantir.gradle.gitversion:gradle-git-version:0.15.0")
    implementation("com.gradle.publish:plugin-publish-plugin:1.1.0")
    implementation("com.github.gmazzo:gradle-buildconfig-plugin:3.1.0")
}
